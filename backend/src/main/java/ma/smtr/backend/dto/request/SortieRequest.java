package ma.smtr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ma.smtr.backend.entity.MissionType;

import java.util.List;

public record SortieRequest(
        @NotNull(message = "Le camion est requis") Long truckId,
        @NotNull(message = "Le chauffeur est requis") Long driverId,
        @NotNull(message = "Le kilométrage de départ est requis") @PositiveOrZero(message = "Le kilométrage de départ doit être positif ou nul") Integer kmDepart,
        @Size(max = 50, message = "L'immatriculation de remorque ne peut pas dépasser 50 caractères") String trailerPlate,
        @Size(max = 255, message = "La destination ne peut pas dépasser 255 caractères") String destination,
        @NotNull(message = "Le type de mission est requis") MissionType missionType,
        @NotEmpty(message = "Le check-list documentaire ne peut pas être vide")
        @Valid List<DocumentCheckItemRequest> documentChecks
) {
}
