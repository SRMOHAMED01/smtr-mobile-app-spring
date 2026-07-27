package ma.smtr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DriverRequest(
        @NotBlank(message = "Le nom est requis") String nom,
        @NotBlank(message = "Le prénom est requis") String prenom,
        String telephone
) {
}
