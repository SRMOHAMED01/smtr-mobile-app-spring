package ma.smtr.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.DocumentTypeRequest;
import ma.smtr.backend.dto.response.DocumentTypeResponse;
import ma.smtr.backend.service.DocumentTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AGENT','ADMIN')")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @GetMapping
    public List<DocumentTypeResponse> findAll() {
        return documentTypeService.findAll().stream().map(DocumentTypeResponse::from).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentTypeResponse> create(@Valid @RequestBody DocumentTypeRequest request) {
        DocumentTypeResponse response = DocumentTypeResponse.from(documentTypeService.create(request));
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
