package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.TruckRequest;
import ma.smtr.backend.entity.Truck;
import ma.smtr.backend.entity.TruckStatus;
import ma.smtr.backend.exception.BusinessException;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    public List<Truck> findAll() {
        return truckRepository.findAll();
    }

    public Truck findById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camion introuvable : id=" + id));
    }

    public Truck create(TruckRequest request) {
        if (truckRepository.existsByMatricule(request.matricule())) {
            throw new BusinessException("Un camion avec cette matricule existe déjà : " + request.matricule());
        }
        Truck truck = new Truck();
        truck.setMatricule(request.matricule());
        truck.setMarque(request.marque());
        truck.setModele(request.modele());
        truck.setStatut(TruckStatus.AU_PARC);
        return truckRepository.save(truck);
    }

    public Truck update(Long id, TruckRequest request) {
        Truck truck = findById(id);
        truck.setMatricule(request.matricule());
        truck.setMarque(request.marque());
        truck.setModele(request.modele());
        return truckRepository.save(truck);
    }

    public void delete(Long id) {
        Truck truck = findById(id);
        if (truck.getStatut() == TruckStatus.EN_MISSION) {
            throw new BusinessException("Impossible de supprimer un camion actuellement en mission");
        }
        truckRepository.delete(truck);
    }
}
