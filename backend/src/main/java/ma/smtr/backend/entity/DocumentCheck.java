package ma.smtr.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_checks")
@Getter
@Setter
@NoArgsConstructor
public class DocumentCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    private boolean present;

    private boolean conforme;

    @Column(length = 500)
    private String justification;

    @ManyToOne
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    private LocalDateTime checkedAt;

    @PrePersist
    protected void onCreate() {
        checkedAt = LocalDateTime.now();
    }
}
