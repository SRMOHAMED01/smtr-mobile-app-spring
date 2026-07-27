package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record EntreeRequest(
        @NotNull(message = "Le kilométrage de retour est requis") Integer kmArrivee,
        boolean anomalie,
        boolean maintenanceNeeded,
        String observations
) {
}
