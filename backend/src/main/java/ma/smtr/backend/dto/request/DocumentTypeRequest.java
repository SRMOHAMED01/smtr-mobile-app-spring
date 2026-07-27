package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DocumentTypeRequest(
        @NotBlank(message = "Le nom du document est requis") String nom,
        boolean obligatoire
) {
}
