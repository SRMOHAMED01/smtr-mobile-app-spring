package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.DocumentTypeRequest;
import ma.smtr.backend.entity.DocumentType;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.DocumentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;

    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    public List<DocumentType> findObligatoires() {
        return documentTypeRepository.findByObligatoireTrue();
    }

    public DocumentType findById(Long id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de document introuvable : id=" + id));
    }

    public DocumentType create(DocumentTypeRequest request) {
        DocumentType type = new DocumentType();
        type.setNom(request.nom());
        type.setObligatoire(request.obligatoire());
        return documentTypeRepository.save(type);
    }

    public void delete(Long id) {
        documentTypeRepository.delete(findById(id));
    }
}
