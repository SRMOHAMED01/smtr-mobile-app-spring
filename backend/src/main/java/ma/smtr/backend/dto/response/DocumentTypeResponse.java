package ma.smtr.backend.dto.response;

import ma.smtr.backend.entity.DocumentType;

public record DocumentTypeResponse(
        Long id,
        String nom,
        boolean obligatoire
) {
    public static DocumentTypeResponse from(DocumentType d) {
        return new DocumentTypeResponse(d.getId(), d.getNom(), d.isObligatoire());
    }
}
