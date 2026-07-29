package ma.smtr.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_audits")
@Getter
@Setter
@NoArgsConstructor
public class MissionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MissionAuditAction action;

    @Enumerated(EnumType.STRING)
    private MissionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private MissionStatus newStatus;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    protected void onCreate() {
        occurredAt = LocalDateTime.now();
    }
}
