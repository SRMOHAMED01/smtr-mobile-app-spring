package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record EntreeRequest(
        @NotNull(message = "Le kilométrage de retour est requis") @PositiveOrZero(message = "Le kilométrage de retour doit être positif ou nul") Integer kmArrivee,
        boolean anomalie,
        boolean maintenanceNeeded,
        @Size(max = 1000, message = "Les observations ne peuvent pas dépasser 1000 caractères") String observations
) {
}
