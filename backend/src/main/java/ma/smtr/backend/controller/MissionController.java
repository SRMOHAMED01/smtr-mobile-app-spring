package ma.smtr.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.EntreeRequest;
import ma.smtr.backend.dto.request.SortieRequest;
import ma.smtr.backend.dto.response.DocumentCheckResponse;
import ma.smtr.backend.dto.response.MissionResponse;
import ma.smtr.backend.entity.Mission;
import ma.smtr.backend.entity.MissionStatus;
import ma.smtr.backend.security.UserPrincipal;
import ma.smtr.backend.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/sortie")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<MissionResponse> enregistrerSortie(@Valid @RequestBody SortieRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal) {
        Mission mission = missionService.enregistrerSortie(request, principal.getUsername());
        return ResponseEntity.status(201).body(toResponse(mission));
    }

    @PostMapping("/{id}/entree")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public MissionResponse enregistrerEntree(@PathVariable Long id, @Valid @RequestBody EntreeRequest request) {
        Mission mission = missionService.enregistrerEntree(id, request);
        return toResponse(mission);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public MissionResponse findById(@PathVariable Long id) {
        return toResponse(missionService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MissionResponse> findAll(@RequestParam(required = false) MissionStatus status) {
        List<Mission> missions = status != null
                ? missionService.findByStatus(status)
                : missionService.findAll();
        return missions.stream().map(this::toResponse).toList();
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MissionResponse> history(@RequestParam Long truckId,
                                          @RequestParam(defaultValue = "TERMINEE") MissionStatus status) {
        return missionService.findHistoryForTruck(truckId, status).stream().map(this::toResponse).toList();
    }

    @GetMapping("/anomalies")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MissionResponse> anomalies() {
        return missionService.findAnomalies().stream().map(this::toResponse).toList();
    }

    private MissionResponse toResponse(Mission mission) {
        List<DocumentCheckResponse> checks = missionService.findDocumentChecks(mission.getId())
                .stream().map(DocumentCheckResponse::from).toList();
        return MissionResponse.from(mission, checks);
    }
}
