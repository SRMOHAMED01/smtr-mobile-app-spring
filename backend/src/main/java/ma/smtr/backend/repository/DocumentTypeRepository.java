package ma.smtr.backend.repository;

import ma.smtr.backend.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    List<DocumentType> findByObligatoireTrue();
}
