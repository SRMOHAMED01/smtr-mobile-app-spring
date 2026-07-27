package ma.smtr.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "truck_id")
    private Truck truck;

    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agent_id")
    private User agent;

    private Integer kmDepart;

    private Integer kmArrivee;

    private String trailerPlate;

    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false)
    private MissionType missionType;

    private LocalDateTime dateDepart;

    private LocalDateTime dateArrivee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;

    // Renseigné au retour
    private Boolean anomalie;

    private Boolean maintenanceNeeded;

    @Column(length = 1000)
    private String observations;
}
