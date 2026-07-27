# SMTR — Backend (Spring Boot)

Backend du système de suivi et contrôle de flotte SMTR : gestion des sorties/entrées
de camions, contrôle documentaire, photos, historique et anomalies.

## Stack

- Java 21, Spring Boot 4.1.0
- Spring Data JPA + PostgreSQL
- Spring Security + JWT (jjwt 0.12.6)
- Bean Validation
- Lombok
- Docker Compose (PostgreSQL local)

## Démarrer le projet

### 1. Lancer PostgreSQL avec Docker

```bash
docker compose up -d
```

Ça démarre un conteneur Postgres 16 avec :
- DB : `smtr_db`
- user / password : `smtr_user` / `smtr_password`
- port `5432`

### 2. Lancer le backend

```bash
./mvnw spring-boot:run
```

Au premier démarrage, Hibernate crée toutes les tables (`ddl-auto=update`), et un
`CommandLineRunner` (`DataSeeder`) insère deux utilisateurs de test :

| username | password  | rôle  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| agent1   | agent123  | AGENT |

⚠️ À changer / retirer avant tout déploiement réel.

### 3. Tester avec Postman / curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Récupère le token, puis utilise-le sur les routes protégées :

```bash
curl http://localhost:8080/api/trucks \
  -H "Authorization: Bearer <TOKEN>"
```

## Endpoints principaux

| Endpoint | Méthode | Rôle requis | Description |
|---|---|---|---|
| `/api/auth/login` | POST | public | Connexion, renvoie un JWT |
| `/api/trucks` | GET | AGENT/ADMIN | Liste des camions |
| `/api/trucks` | POST | ADMIN | Créer un camion |
| `/api/trucks/{id}` | PUT/DELETE | ADMIN | Modifier / supprimer |
| `/api/drivers` | GET/POST | AGENT/ADMIN | Chauffeurs |
| `/api/document-types` | GET | AGENT/ADMIN | Types de documents |
| `/api/document-types` | POST | ADMIN | Ajouter un type de document |
| `/api/missions/sortie` | POST | AGENT/ADMIN | Enregistrer une sortie (contrôle doc inclus) |
| `/api/missions/{id}/entree` | POST | AGENT/ADMIN | Enregistrer le retour |
| `/api/missions/{id}` | GET | AGENT/ADMIN | Détail d'une mission |
| `/api/missions` | GET | ADMIN | Historique / recherche |
| `/api/missions/history?truckId=&status=` | GET | ADMIN | Historique d'un camion |
| `/api/missions/anomalies` | GET | ADMIN | Missions avec anomalie/maintenance |
| `/api/missions/{id}/photos` | POST (multipart) | AGENT/ADMIN | Ajouter une photo |
| `/api/photos/{id}` | GET | AGENT/ADMIN | Récupérer une photo (binaire) |

## Scénario de test complet

1. `POST /api/auth/login` avec `admin` → récupérer le token
2. `POST /api/document-types` → créer quelques types obligatoires (Contrat, Assurance, Carte grise)
3. `POST /api/trucks` → créer un camion
4. `POST /api/drivers` → créer un chauffeur
5. Login avec `agent1`
6. `POST /api/missions/sortie` avec le check-list documentaire complet
   - Si un document obligatoire manque dans le check-list → `422`
   - Si un document est `present: false` sans `justification` → `422`
   - Si le camion est déjà `EN_MISSION` → `422`
7. `POST /api/missions/{id}/entree` avec `kmArrivee`
   - Si `kmArrivee < kmDepart` → `422`
8. `GET /api/missions/anomalies` (en tant qu'admin) pour vérifier le signalement

## Règles métier implémentées (`MissionService`)

- Un camion déjà `EN_MISSION` ne peut pas ressortir
- Tout document obligatoire doit être couvert par le check-list soumis
- Document absent (`present=false`) → `justification` obligatoire, sinon exception
- Une sortie autorisée crée une `Mission` (statut `OUVERTE`) et passe le camion à `EN_MISSION`
- Une sortie refusée ne crée aucune mission
- `kmArrivee >= kmDepart`, sinon exception
- Une entrée clôture toujours la mission (`TERMINEE`) et repasse le camion à `AU_PARC`
- Anomalie / besoin de maintenance signalés sur la mission pour le superviseur

## Ce qui reste à faire (prochaines étapes)

- Tests unitaires (JUnit) sur `MissionService` pour chaque règle métier
- Swagger/OpenAPI pour documenter l'API
- Flyway pour remplacer `ddl-auto=update` par des migrations versionnées
- Dockerfile pour containeriser le backend lui-même (déploiement)
- Rafraîchissement de token (refresh token) si les sessions doivent durer plus longtemps
- Connexion avec l'application mobile (React Native / Flutter)
