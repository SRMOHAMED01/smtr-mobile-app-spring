package ma.smtr.backend.repository;

import ma.smtr.backend.entity.MissionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionAuditRepository extends JpaRepository<MissionAudit, Long> {
    List<MissionAudit> findByMissionIdOrderByOccurredAtAsc(Long missionId);
}
