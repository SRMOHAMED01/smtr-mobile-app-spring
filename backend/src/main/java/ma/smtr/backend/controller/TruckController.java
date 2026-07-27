package ma.smtr.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.TruckRequest;
import ma.smtr.backend.dto.response.TruckResponse;
import ma.smtr.backend.service.TruckService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AGENT','ADMIN')")
public class TruckController {

    private final TruckService truckService;

    @GetMapping
    public List<TruckResponse> findAll() {
        return truckService.findAll().stream().map(TruckResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TruckResponse findById(@PathVariable Long id) {
        return TruckResponse.from(truckService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TruckResponse> create(@Valid @RequestBody TruckRequest request) {
        TruckResponse response = TruckResponse.from(truckService.create(request));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TruckResponse update(@PathVariable Long id, @Valid @RequestBody TruckRequest request) {
        return TruckResponse.from(truckService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        truckService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
