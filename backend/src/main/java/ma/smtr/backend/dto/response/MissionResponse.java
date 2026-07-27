package ma.smtr.backend.dto.response;

import ma.smtr.backend.entity.Mission;
import ma.smtr.backend.entity.MissionStatus;
import ma.smtr.backend.entity.MissionType;

import java.time.LocalDateTime;
import java.util.List;

public record MissionResponse(
        Long id,
        String truckMatricule,
        String driverNomComplet,
        String agentUsername,
        Integer kmDepart,
        Integer kmArrivee,
        String trailerPlate,
        String destination,
        MissionType missionType,
        LocalDateTime dateDepart,
        LocalDateTime dateArrivee,
        MissionStatus status,
        Boolean anomalie,
        Boolean maintenanceNeeded,
        String observations,
        List<DocumentCheckResponse> documentChecks
) {
    public static MissionResponse from(Mission m, List<DocumentCheckResponse> checks) {
        return new MissionResponse(
                m.getId(),
                m.getTruck().getMatricule(),
                m.getDriver().getPrenom() + " " + m.getDriver().getNom(),
                m.getAgent().getUsername(),
                m.getKmDepart(),
                m.getKmArrivee(),
                m.getTrailerPlate(),
                m.getDestination(),
                m.getMissionType(),
                m.getDateDepart(),
                m.getDateArrivee(),
                m.getStatus(),
                m.getAnomalie(),
                m.getMaintenanceNeeded(),
                m.getObservations(),
                checks
        );
    }
}
