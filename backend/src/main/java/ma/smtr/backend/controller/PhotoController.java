package ma.smtr.backend.controller;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.entity.Photo;
import ma.smtr.backend.entity.PhotoType;
import ma.smtr.backend.service.PhotoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ma.smtr.backend.security.UserPrincipal;

@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/api/missions/{missionId}/photos")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<Long> upload(@PathVariable Long missionId,
                                        @RequestParam(required = false) Long documentCheckId,
                                        @RequestParam PhotoType type,
                                        @RequestParam("file") MultipartFile file,
                                        @AuthenticationPrincipal UserPrincipal principal) {
        Photo photo = photoService.upload(missionId, documentCheckId, type, file, principal.getUsername());
        return ResponseEntity.status(201).body(photo.getId());
    }

    @GetMapping("/api/photos/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<byte[]> get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        Photo photo = photoService.findById(id, principal.getUsername());
        MediaType mediaType = photo.getContentType() != null
                ? MediaType.parseMediaType(photo.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"photo-" + id + "\"")
                .body(photo.getPhotoData());
    }
}
