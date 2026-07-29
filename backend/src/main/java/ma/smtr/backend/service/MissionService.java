package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.DocumentCheckItemRequest;
import ma.smtr.backend.dto.request.EntreeRequest;
import ma.smtr.backend.dto.request.SortieRequest;
import ma.smtr.backend.entity.*;
import ma.smtr.backend.exception.BusinessException;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentCheckRepository documentCheckRepository;
    private final MissionAuditRepository missionAuditRepository;
    private final MissionAccessService missionAccessService;

    // ------------------------------------------------------------------
    // Règle 1 + 2 : contrôle documentaire puis création de mission
    // ------------------------------------------------------------------
    @Transactional
    public Mission enregistrerSortie(SortieRequest request, String agentUsername) {

        Truck truck = truckRepository.findById(request.truckId())
                .orElseThrow(() -> new ResourceNotFoundException("Camion introuvable : id=" + request.truckId()));

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur introuvable : id=" + request.driverId()));

        User agent = userRepository.findByUsername(agentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable : " + agentUsername));

        // Règle : un camion déjà en mission ne peut pas ressortir
        if (truck.getStatut() == TruckStatus.EN_MISSION) {
            throw new BusinessException("Le camion " + truck.getMatricule() + " est déjà en mission");
        }

        if (missionRepository.existsByDriverIdAndStatus(request.driverId(), MissionStatus.OUVERTE)) {
            throw new BusinessException("Le chauffeur sélectionné est déjà affecté à une mission ouverte");
        }

        // Règle : tout document obligatoire doit être couvert par le check-list
        List<DocumentType> documentsObligatoires = documentTypeRepository.findByObligatoireTrue();
        Set<Long> idsSoumis = request.documentChecks().stream()
                .map(DocumentCheckItemRequest::documentTypeId)
                .collect(Collectors.toSet());

        if (idsSoumis.size() != request.documentChecks().size()) {
            throw new BusinessException("Un type de document ne peut être contrôlé qu'une seule fois par sortie");
        }

        for (DocumentType obligatoire : documentsObligatoires) {
            if (!idsSoumis.contains(obligatoire.getId())) {
                throw new BusinessException(
                        "Le document obligatoire '" + obligatoire.getNom() + "' n'a pas été contrôlé");
            }
        }

        // Règle : document absent (present=false) => justification obligatoire
        for (DocumentCheckItemRequest item : request.documentChecks()) {
            if (!item.present() && (item.justification() == null || item.justification().isBlank())) {
                DocumentType type = documentTypeRepository.findById(item.documentTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Type de document introuvable : id=" + item.documentTypeId()));
                throw new BusinessException(
                        "Document absent '" + type.getNom() + "' : une justification est obligatoire");
            }
        }

        // Tout est conforme : on crée la mission et les document checks
        Mission mission = new Mission();
        mission.setTruck(truck);
        mission.setDriver(driver);
        mission.setAgent(agent);
        mission.setKmDepart(request.kmDepart());
        mission.setTrailerPlate(request.trailerPlate());
        mission.setDestination(request.destination());
        mission.setMissionType(request.missionType());
        mission.setDateDepart(LocalDateTime.now());
        mission.setStatus(MissionStatus.OUVERTE);
        mission = missionRepository.save(mission);

        for (DocumentCheckItemRequest item : request.documentChecks()) {
            DocumentType type = documentTypeRepository.findById(item.documentTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Type de document introuvable : id=" + item.documentTypeId()));

            DocumentCheck check = new DocumentCheck();
            check.setMission(mission);
            check.setDocumentType(type);
            check.setPresent(item.present());
            check.setConforme(item.conforme());
            check.setJustification(item.justification());
            check.setCheckedBy(agent);
            documentCheckRepository.save(check);
        }

        recordAudit(mission, agent, MissionAuditAction.DOCUMENTS_CONTROLES, null, MissionStatus.OUVERTE,
                "Contrôle de " + request.documentChecks().size() + " document(s) enregistré");

        // Le camion part en mission
        truck.setStatut(TruckStatus.EN_MISSION);
        truckRepository.save(truck);

        recordAudit(mission, agent, MissionAuditAction.SORTIE_ENREGISTREE, null, MissionStatus.OUVERTE,
                "Sortie enregistrée pour le camion " + truck.getMatricule());

        return mission;
    }

    // ------------------------------------------------------------------
    // Règle 3 + 4 + 5 : retour du camion, clôture de la mission
    // ------------------------------------------------------------------
    @Transactional
    public Mission enregistrerEntree(Long missionId, EntreeRequest request, String actorUsername) {

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable : id=" + missionId));

        User actor = missionAccessService.requireAccess(mission, actorUsername);

        if (mission.getStatus() != MissionStatus.OUVERTE) {
            throw new BusinessException("Cette mission est déjà clôturée");
        }

        // Règle : km retour >= km départ
        if (request.kmArrivee() < mission.getKmDepart()) {
            throw new BusinessException(
                    "Le kilométrage de retour (" + request.kmArrivee() +
                            ") doit être supérieur ou égal au kilométrage de départ (" + mission.getKmDepart() + ")");
        }

        mission.setKmArrivee(request.kmArrivee());
        mission.setDateArrivee(LocalDateTime.now());
        mission.setAnomalie(request.anomalie());
        mission.setMaintenanceNeeded(request.maintenanceNeeded());
        mission.setObservations(request.observations());
        mission.setStatus(MissionStatus.TERMINEE);
        missionRepository.save(mission);

        // Le camion repasse au parc, que la maintenance soit nécessaire ou non
        // (le besoin de maintenance est juste signalé sur la mission pour le superviseur)
        Truck truck = mission.getTruck();
        truck.setStatut(TruckStatus.AU_PARC);
        truckRepository.save(truck);

        recordAudit(mission, actor, MissionAuditAction.ENTREE_ENREGISTREE,
                MissionStatus.OUVERTE, MissionStatus.TERMINEE, "Retour enregistré");

        return mission;
    }

    public Mission findById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable : id=" + id));
    }

    public Mission findAccessibleById(Long id, String username) {
        Mission mission = findById(id);
        missionAccessService.requireAccess(mission, username);
        return mission;
    }

    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    public List<Mission> findByStatus(MissionStatus status) {
        return missionRepository.findByStatus(status);
    }

    public List<Mission> findHistoryForTruck(Long truckId, MissionStatus status) {
        return missionRepository.findByTruckIdAndStatus(truckId, status);
    }

    public List<DocumentCheck> findDocumentChecks(Long missionId) {
        return documentCheckRepository.findByMissionId(missionId);
    }

    public List<Mission> findAnomalies() {
        return missionRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getAnomalie()) || Boolean.TRUE.equals(m.getMaintenanceNeeded()))
                .toList();
    }

    private void recordAudit(Mission mission, User actor, MissionAuditAction action,
                             MissionStatus previousStatus, MissionStatus newStatus, String details) {
        MissionAudit audit = new MissionAudit();
        audit.setMission(mission);
        audit.setPerformedBy(actor);
        audit.setAction(action);
        audit.setPreviousStatus(previousStatus);
        audit.setNewStatus(newStatus);
        audit.setDetails(details);
        missionAuditRepository.save(audit);
    }
}
