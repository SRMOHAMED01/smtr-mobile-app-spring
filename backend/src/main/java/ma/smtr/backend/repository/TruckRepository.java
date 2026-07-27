package ma.smtr.backend.repository;

import ma.smtr.backend.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TruckRepository extends JpaRepository<Truck, Long> {
    Optional<Truck> findByMatricule(String matricule);
    boolean existsByMatricule(String matricule);
}
