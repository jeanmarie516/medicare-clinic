# 🏥 MediCare - Système de Gestion de Clinique Médicale

Application web complète de gestion de clinique médicale avec **React.js** (frontend) et **Spring Boot 3** (backend).

## ✨ Fonctionnalités

- ✅ **Gestion des patients** (CRUD, recherche, historique médical)
- ✅ **Gestion des rendez-vous** (calendrier interactif, vérification conflits, règle 24h)
- ✅ **Prescriptions médicales** (création dynamique, génération PDF)
- ✅ **Facturation** (lignes dynamiques, TVA, suivi paiements)
- ✅ **Messagerie interne** (chat temps réel avec WebSocket)
- ✅ **Notifications** (temps réel, badge non lus)
- ✅ **Tableaux de bord** (statistiques, graphiques Recharts)
- ✅ **Sécurité** (authentification JWT, 3 rôles: Admin/Médecin/Secrétaire)
- ✅ **Génération de PDF** (prescriptions, factures)
- ✅ **Journal d'audit** (traçabilité de toutes les actions)

## 🛠️ Technologies

| Frontend | Backend |
|----------|---------|
| React 18 + TypeScript | Spring Boot 3.2 |
| Tailwind CSS 3 | Spring Security + JWT |
| Zustand (state) | Spring Data JPA |
| React Big Calendar | PostgreSQL |
| Recharts (graphiques) | WebSocket (STOMP) |
| React Hook Form + Zod | iText PDF |
| STOMP.js / SockJS | Swagger/OpenAPI |
| Lucide React (icônes) | Docker |

## 📋 Prérequis

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.9+
- Docker (optionnel)

## 🚀 Installation

### 1. Cloner le projet
```bash
git clone <votre-repo>
cd medicare-clinic
```

### 2. Base de données PostgreSQL
```sql
CREATE DATABASE medicare_db;
CREATE USER medicare_user WITH PASSWORD 'medicare_pass123';
GRANT ALL PRIVILEGES ON DATABASE medicare_db TO medicare_user;
```

### 3. Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Le backend démarre sur `http://localhost:8080`

### 4. Frontend
```bash
cd frontend
npm install
npm run dev
```
Le frontend démarre sur `http://localhost:5173`

### 5. Avec Docker
```bash
docker-compose up -d
```

## 🔑 Comptes de Démonstration

| Rôle | Email | Mot de passe |
|------|-------|-------------|
| **Admin** | admin@medicare.sn | password123 |
| **Médecin** | aminata.ndiaye@medicare.sn | password123 |
| **Médecin** | ousmane.fall@medicare.sn | password123 |
| **Médecin** | fatou.sow@medicare.sn | password123 |
| **Médecin** | ibrahima.ba@medicare.sn | password123 |
| **Médecin** | marieme.thiam@medicare.sn | password123 |
| **Secrétaire** | aicha.gueye@medicare.sn | password123 |
| **Secrétaire** | moussa.dia@medicare.sn | password123 |
| **Secrétaire** | rokhaya.kane@medicare.sn | password123 |

## 📚 Documentation API (Swagger)

Une fois le backend lancé :
- Swagger UI : `http://localhost:8080/api/v1/swagger-ui.html`
- Documentation OpenAPI : `http://localhost:8080/api/v1/v3/api-docs`

## 📁 Structure du Projet

```
medicare-clinic/
├── backend/                          # Spring Boot Backend
│   ├── src/main/java/com/medicare/
│   │   ├── config/                   # Config (CORS, Swagger, WebSocket)
│   │   ├── controller/               # REST Controllers (9)
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── entity/                   # JPA Entities (8)
│   │   ├── exception/                # Exception Handler
│   │   ├── pdf/                      # Générateurs PDF
│   │   ├── repository/               # Spring Data Repositories
│   │   ├── security/                 # JWT + Spring Security
│   │   ├── service/                  # Services métier (10)
│   │   └── websocket/                # WebSocket Controllers
│   └── src/main/resources/
│       ├── application.properties
│       └── db/init.sql               # Script BDD + seed data
├── frontend/                         # React.js Frontend
│   └── src/
│       ├── components/ui/            # Composants réutilisables (17)
│       ├── hooks/                    # Hooks personnalisés
│       ├── layouts/                  # Layouts (Dashboard, Auth)
│       ├── pages/                    # Pages (20+)
│       ├── services/                 # Services API
│       ├── store/                    # Stores Zustand
│       └── types/                    # Types TypeScript
├── docker-compose.yml
└── README.md
```

## 🔒 Règles Métier

- **Rendez-vous** : Pas de doublons médecin, max 1 RDV par patient par jour, annulation 24h avant
- **Prescriptions** : Médecin uniquement, minimum 1 médicament
- **Factures** : Création automatique après RDV, relance après 7 jours
- **Sécurité** : Admin (tout), Médecin (patients/RDV/prescriptions), Secrétaire (RDV/factures)

## 🌍 API Endpoints Principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/login` | Connexion |
| POST | `/auth/register` | Création compte |
| GET | `/patients` | Liste patients |
| POST | `/patients` | Créer patient |
| GET | `/rendez-vous/calendrier` | RDV pour calendrier |
| POST | `/rendez-vous` | Créer RDV |
| POST | `/rendez-vous/{id}/annuler` | Annuler RDV |
| POST | `/prescriptions` | Créer prescription |
| POST | `/factures` | Créer facture |
| POST | `/factures/{id}/payer` | Payer facture |
| GET | `/messages/conversation/{userId}` | Conversation chat |
| GET | `/dashboard/stats` | Stats admin |

## 📄 Licence

Projet éducatif - MediCare Clinic Management System
