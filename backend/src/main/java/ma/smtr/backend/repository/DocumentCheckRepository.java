package ma.smtr.backend.repository;

import ma.smtr.backend.entity.DocumentCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentCheckRepository extends JpaRepository<DocumentCheck, Long> {
    List<DocumentCheck> findByMissionId(Long missionId);
}
