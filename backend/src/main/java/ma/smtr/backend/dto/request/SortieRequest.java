package ma.smtr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ma.smtr.backend.entity.MissionType;

import java.util.List;

public record SortieRequest(
        @NotNull(message = "Le camion est requis") Long truckId,
        @NotNull(message = "Le chauffeur est requis") Long driverId,
        @NotNull(message = "Le kilométrage de départ est requis") Integer kmDepart,
        String trailerPlate,
        String destination,
        @NotNull(message = "Le type de mission est requis") MissionType missionType,
        @NotEmpty(message = "Le check-list documentaire ne peut pas être vide")
        @Valid List<DocumentCheckItemRequest> documentChecks
) {
}
