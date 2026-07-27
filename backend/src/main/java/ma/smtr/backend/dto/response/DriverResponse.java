package ma.smtr.backend.dto.response;

import ma.smtr.backend.entity.Driver;

public record DriverResponse(
        Long id,
        String nom,
        String prenom,
        String telephone
) {
    public static DriverResponse from(Driver d) {
        return new DriverResponse(d.getId(), d.getNom(), d.getPrenom(), d.getTelephone());
    }
}
