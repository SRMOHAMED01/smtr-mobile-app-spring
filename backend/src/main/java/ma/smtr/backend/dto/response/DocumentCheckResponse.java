package ma.smtr.backend.dto.response;

import ma.smtr.backend.entity.DocumentCheck;

public record DocumentCheckResponse(
        Long id,
        Long documentTypeId,
        String documentTypeNom,
        boolean present,
        boolean conforme,
        String justification
) {
    public static DocumentCheckResponse from(DocumentCheck dc) {
        return new DocumentCheckResponse(
                dc.getId(),
                dc.getDocumentType().getId(),
                dc.getDocumentType().getNom(),
                dc.isPresent(),
                dc.isConforme(),
                dc.getJustification()
        );
    }
}
