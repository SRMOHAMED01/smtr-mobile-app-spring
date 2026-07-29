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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MissionRepository missionRepository;
    private final DocumentCheckRepository documentCheckRepository;
    private final MissionAccessService missionAccessService;

    @Transactional
    public Photo upload(Long missionId, Long documentCheckId, PhotoType type, MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Une photo non vide est requise");
        }

        validateImage(file);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable : id=" + missionId));
        missionAccessService.requireAccess(mission, username);

        DocumentCheck documentCheck = null;
        if (documentCheckId != null) {
            documentCheck = documentCheckRepository.findById(documentCheckId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contrôle documentaire introuvable : id=" + documentCheckId));
            if (!documentCheck.getMission().getId().equals(missionId)) {
                throw new BusinessException("Le contrôle documentaire ne correspond pas à la mission sélectionnée");
            }
        }

        if (type == PhotoType.PHOTO_DOCUMENT && documentCheck == null) {
            throw new BusinessException("Une photo documentaire doit être liée à un contrôle documentaire");
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

    public Photo findById(Long id, String username) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo introuvable : id=" + id));
        missionAccessService.requireAccess(photo.getMission(), username);
        return photo;
    }

    public List<Photo> findByMission(Long missionId) {
        return photoRepository.findByMissionId(missionId);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/png"))) {
            throw new BusinessException("Seules les images JPEG et PNG sont autorisées");
        }

        try (InputStream inputStream = file.getInputStream()) {
            if (ImageIO.read(inputStream) == null) {
                throw new BusinessException("Le fichier envoyé n'est pas une image valide");
            }
        } catch (IOException ex) {
            throw new BusinessException("Impossible de lire l'image envoyée");
        }
    }
}
