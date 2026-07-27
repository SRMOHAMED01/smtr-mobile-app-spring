package ma.smtr.backend.dto.response;

public record LoginResponse(
        String token,
        String username,
        String role
) {
}
