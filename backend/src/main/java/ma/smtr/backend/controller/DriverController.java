package ma.smtr.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.DriverRequest;
import ma.smtr.backend.dto.response.DriverResponse;
import ma.smtr.backend.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AGENT','ADMIN')")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public List<DriverResponse> findAll() {
        return driverService.findAll().stream().map(DriverResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DriverResponse findById(@PathVariable Long id) {
        return DriverResponse.from(driverService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        DriverResponse response = DriverResponse.from(driverService.create(request));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return DriverResponse.from(driverService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
