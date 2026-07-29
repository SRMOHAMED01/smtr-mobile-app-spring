package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Un item du check-list documentaire soumis lors d'une sortie.
 * Règle métier : si present = false, justification devient obligatoire (validé en service).
 */
public record DocumentCheckItemRequest(
        @NotNull(message = "Le type de document est requis") Long documentTypeId,
        boolean present,
        boolean conforme,
        @Size(max = 500, message = "La justification ne peut pas dépasser 500 caractères") String justification
) {
}
