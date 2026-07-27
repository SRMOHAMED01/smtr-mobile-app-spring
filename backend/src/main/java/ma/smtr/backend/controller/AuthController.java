package ma.smtr.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.LoginRequest;
import ma.smtr.backend.dto.response.LoginResponse;
import ma.smtr.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
