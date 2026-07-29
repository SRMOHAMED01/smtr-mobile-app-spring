package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.entity.Mission;
import ma.smtr.backend.entity.Role;
import ma.smtr.backend.entity.User;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionAccessService {

    private final UserRepository userRepository;

    public User requireAccess(Mission mission, String username) {
        User actor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + username));

        if (actor.getRole() == Role.AGENT && !mission.getAgent().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Cette mission n'appartient pas à l'agent connecté");
        }
        return actor;
    }
}
