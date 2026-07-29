package ma.smtr.backend.repository;

import ma.smtr.backend.entity.Mission;
import ma.smtr.backend.entity.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByTruckIdAndStatus(Long truckId, MissionStatus status);
    List<Mission> findByStatus(MissionStatus status);
    Optional<Mission> findFirstByTruckIdAndStatus(Long truckId, MissionStatus status);
    boolean existsByDriverIdAndStatus(Long driverId, MissionStatus status);
}
