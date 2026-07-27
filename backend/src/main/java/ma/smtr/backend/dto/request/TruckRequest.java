package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TruckRequest(
        @NotBlank(message = "La matricule est requise") String matricule,
        String marque,
        String modele
) {
}
