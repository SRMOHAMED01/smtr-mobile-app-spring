package ma.smtr.backend.config;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.entity.Role;
import ma.smtr.backend.entity.User;
import ma.smtr.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée un utilisateur ADMIN par défaut au premier démarrage, pour pouvoir
 * tester l'API immédiatement (login -> /api/auth/login).
 *
 * ⚠️ À retirer ou sécuriser avant un vrai déploiement en production.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">> Utilisateur admin créé (username: admin / password: admin123) — à changer !");
        }

        if (!userRepository.existsByUsername("agent1")) {
            User agent = new User();
            agent.setUsername("agent1");
            agent.setPasswordHash(passwordEncoder.encode("agent123"));
            agent.setRole(Role.AGENT);
            userRepository.save(agent);
            System.out.println(">> Utilisateur agent1 créé (username: agent1 / password: agent123) — à changer !");
        }
    }
}
