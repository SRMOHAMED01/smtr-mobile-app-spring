package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.entity.DocumentCheck;
import ma.smtr.backend.entity.Mission;
import ma.smtr.backend.entity.Photo;
import ma.smtr.backend.entity.PhotoType;
import ma.smtr.backend.exception.BusinessException;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.DocumentCheckRepository;
import ma.smtr.backend.repository.MissionRepository;
import ma.smtr.backend.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MissionRepository missionRepository;
    private final DocumentCheckRepository documentCheckRepository;

    public Photo upload(Long missionId, Long documentCheckId, PhotoType type, MultipartFile file) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable : id=" + missionId));

        DocumentCheck documentCheck = null;
        if (documentCheckId != null) {
            documentCheck = documentCheckRepository.findById(documentCheckId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contrôle documentaire introuvable : id=" + documentCheckId));
        }

        try {
            Photo photo = new Photo();
            photo.setMission(mission);
            photo.setDocumentCheck(documentCheck);
            photo.setPhotoData(file.getBytes());
            photo.setContentType(file.getContentType());
            photo.setType(type);
            return photoRepository.save(photo);
        } catch (IOException e) {
            throw new BusinessException("Impossible de lire le fichier envoyé : " + e.getMessage());
        }
    }

    public Photo findById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo introuvable : id=" + id));
    }

    public List<Photo> findByMission(Long missionId) {
        return photoRepository.findByMissionId(missionId);
    }
}
