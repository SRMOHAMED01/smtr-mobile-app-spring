package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.LoginRequest;
import ma.smtr.backend.dto.response.LoginResponse;
import ma.smtr.backend.security.JwtService;
import ma.smtr.backend.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        // Déclenche UserDetailsServiceImpl + vérifie le mot de passe (BCrypt).
        // Lève BadCredentialsException si invalide -> gérée par GlobalExceptionHandler -> 401.
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new LoginResponse(token, principal.getUsername(), principal.getUser().getRole().name());
    }
}
