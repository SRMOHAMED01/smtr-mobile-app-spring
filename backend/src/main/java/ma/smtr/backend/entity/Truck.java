package ma.smtr.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trucks")
@Getter
@Setter
@NoArgsConstructor
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private String matricule;

    private String marque;

    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TruckStatus statut = TruckStatus.AU_PARC;
}
