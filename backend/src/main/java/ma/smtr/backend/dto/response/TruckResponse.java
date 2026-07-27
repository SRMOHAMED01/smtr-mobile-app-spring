package ma.smtr.backend.dto.response;

import ma.smtr.backend.entity.Truck;
import ma.smtr.backend.entity.TruckStatus;

public record TruckResponse(
        Long id,
        String matricule,
        String marque,
        String modele,
        TruckStatus statut
) {
    public static TruckResponse from(Truck t) {
        return new TruckResponse(t.getId(), t.getMatricule(), t.getMarque(), t.getModele(), t.getStatut());
    }
}
