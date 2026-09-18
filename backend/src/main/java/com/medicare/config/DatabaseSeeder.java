package com.medicare.config;

import com.medicare.entity.*;
import com.medicare.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final Random RANDOM = new Random(42);

    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final FactureRepository factureRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, MedecinRepository medecinRepository,
                          PatientRepository patientRepository, RendezVousRepository rendezVousRepository,
                          FactureRepository factureRepository, PrescriptionRepository prescriptionRepository,
                          NotificationRepository notificationRepository, AuditLogRepository auditLogRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository; this.rendezVousRepository = rendezVousRepository;
        this.factureRepository = factureRepository; this.prescriptionRepository = prescriptionRepository;
        this.notificationRepository = notificationRepository; this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = ensureAdmin();
        seedMedecins();
        seedSecretaires();
        seedPatients(admin);

        List<Patient> patients = patientRepository.findAll();
        List<Medecin> medecins = medecinRepository.findAll();
        List<User> users = userRepository.findAll();

        seedRendezVous(patients, medecins, admin);
        seedFactures(patients, medecins, admin);
        seedPrescriptions(patients, medecins);
        seedNotifications(users);
        seedAuditLogs(users, admin);

        // Deuxième vague
        seedMorePatients(admin);
        List<Patient> allPatients = patientRepository.findAll();
        List<Medecin> allMedecins = medecinRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        seedMoreRendezVous(allPatients, allMedecins, admin);
        seedMoreFactures(allPatients, allMedecins, admin);
        seedMorePrescriptions(allPatients, allMedecins);
        seedMoreNotifications(allUsers);
        seedMoreAuditLogs(allUsers, admin);

        log.info("✅ Base de données initialisée avec toutes les données de démonstration");
    }

    // ══════════════════════════════════════════════
    // ADMIN
    // ══════════════════════════════════════════════

    private User ensureAdmin() {
        Optional<User> existing = userRepository.findByEmail("jkaremamana@gmail.com");
        if (existing.isPresent()) {
            User admin = existing.get();
            if (!passwordEncoder.matches("Bonheur0407@", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("Bonheur0407@"));
                userRepository.save(admin);
            }
            log.info("✓ Admin déjà présent : jkaremamana@gmail.com");
            return admin;
        }
        User admin = User.builder().nom("KAREMAMANA").prenom("Jean-Marie")
                .email("jkaremamana@gmail.com").password(passwordEncoder.encode("Bonheur0407@"))
                .role(Role.ADMIN).actif(true).build();
        admin = userRepository.save(admin);
        log.info("✓ Admin créé : jkaremamana@gmail.com / Bonheur0407@");
        return admin;
    }

    // ══════════════════════════════════════════════
    // MÉDECINS
    // ══════════════════════════════════════════════

    private void seedMedecins() {
        createMedecin("Diallo","Fatou","fatou.diallo@medicare.sn","password123","Cardiologue","ORD-001",50000,12);
        createMedecin("Ba","Ousmane","ousmane.ba@medicare.sn","password123","Pédiatre","ORD-002",35000,8);
        createMedecin("Sow","Aïssatou","aissatou.sow@medicare.sn","password123","Gynécologue","ORD-003",45000,15);
        createMedecin("Gueye","Mamadou","mamadou.gueye@medicare.sn","password123","Ophtalmologue","ORD-004",40000,10);
        createMedecin("Diop","Ndeye","ndeye.diop@medicare.sn","password123","Dermatologue","ORD-005",30000,6);
        createMedecin("Yangao","Alex","Yangao@gmail.com","Yangao@1234","Dermatologue","ORD-006",35000,7);
        createMedecin("Kamanz","Justin","kamanzijustin@gmail.com","justin1234@","Généraliste","ORD-007",25000,5);
        createMedecin("Guela","Lionel","guelalionnel@gmail.com","lionnel@2587","Neurologue","ORD-008",55000,9);
    }

    private void createMedecin(String nom, String prenom, String email, String rawPassword,
                                String specialite, String numeroOrdre, int tarif, int experience) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            User u = existing.get();
            if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
                u.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(u);
                log.info("✓ Mot de passe réparé pour Dr. {} {}", prenom, nom);
            }
            log.info("✓ Médecin déjà présent : {} {}", prenom, nom);
            return;
        }
        User user = User.builder().nom(nom).prenom(prenom).email(email)
                .password(passwordEncoder.encode(rawPassword)).role(Role.MEDECIN).actif(true).build();
        user = userRepository.save(user);
        medecinRepository.save(Medecin.builder().user(user).specialite(specialite)
                .numeroOrdre(numeroOrdre).disponible(true)
                .tarifConsultation(BigDecimal.valueOf(tarif)).anneesExperience(experience).build());
        log.info("✓ Médecin créé : Dr. {} {} - {}", prenom, nom, specialite);
    }

    // ══════════════════════════════════════════════
    // SECRÉTAIRES
    // ══════════════════════════════════════════════

    private void seedSecretaires() {
        createSecretaire("Ndiaye", "Aminata", "aminata.ndiaye@medicare.sn", "password123");
        createSecretaire("Diop", "Marie", "marie.diop@medicare.sn", "password123");
    }

    private void createSecretaire(String nom, String prenom, String email, String rawPassword) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            User u = existing.get();
            if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
                u.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(u);
                log.info("✓ Mot de passe réparé pour {} {}", prenom, nom);
            }
            log.info("✓ Secrétaire déjà présente : {} {}", prenom, nom);
            return;
        }
        userRepository.save(User.builder().nom(nom).prenom(prenom)
                .email(email).password(passwordEncoder.encode(rawPassword))
                .role(Role.SECRETAIRE).actif(true).build());
        log.info("✓ Secrétaire créée : {} {}", prenom, nom);
    }

    // ══════════════════════════════════════════════
    // PATIENTS - Vague 1
    // ══════════════════════════════════════════════

    private void seedPatients(User admin) {
        if (patientRepository.count() >= 14) { log.info("✓ Patients déjà présents (≥14)"); return; }
        addPatient("Fall","Mamadou",1985,6,15,"M","77 123 45 67","mamadou.fall@email.sn",
                "Dakar, Sicap Liberté","A+","Aucune","Hypertension","Aminata Fall","77 987 65 43",admin);
        addPatient("Sy","Khadidiatou",1992,3,22,"F","78 234 56 78","khadidiatou.sy@email.sn",
                "Dakar, Mermoz","O+","Pénicilline","Aucun","Ousmane Sy","77 876 54 32",admin);
        addPatient("Touré","Ibrahima",1978,11,8,"M","76 345 67 89","ibrahima.toure@email.sn",
                "Thiès, Escale","B+","Aucune","Diabète type 2","Aïcha Touré","78 765 43 21",admin);
        addPatient("Dieng","Awa",2000,7,30,"F","77 456 78 90","awa.dieng@email.sn",
                "Dakar, Ouakam","AB-","Sulfamides","Asthme","Moussa Dieng","76 654 32 10",admin);
        addPatient("Cissé","Souleymane",1965,1,14,"M","78 567 89 01","souleymane.cisse@email.sn",
                "Saint-Louis, Guet Ndar","A-","Aucune","Arthrite","Fatou Cissé","77 543 21 09",admin);
        addPatient("Ndiaye","Fatoumata",1990,5,20,"F","77 111 22 33","fatou.ndiaye@email.sn",
                "Dakar, Hann Mariste","O-","Aucune","Migraine chronique","Moussa Ndiaye","77 999 88 77",admin);
        addPatient("Faye","Modou",1982,9,12,"M","78 222 33 44","modou.faye@email.sn",
                "Rufisque, Cité","B-","Ibuprofène","Lombalgie","Aminata Faye","76 888 77 66",admin);
        addPatient("Thiam","Marième",1995,12,3,"F","76 333 44 55","marieme.thiam@email.sn",
                "Dakar, Almadies","AB+","Aucune","Aucun","Omar Thiam","77 777 66 55",admin);
        addPatient("Sarr","Aliou",1970,4,28,"M","77 444 55 66","aliou.sarr@email.sn",
                "Dakar, Grand Yoff","A+","Pénicilline","Hypertension, Diabète","Ramatoulaye Sarr","78 666 55 44",admin);
        addPatient("Mendy","Sophie",2002,8,15,"F","78 555 66 77","sophie.mendy@email.sn",
                "Dakar, Sacré-Cœur","O+","Aucune","Aucun","Pierre Mendy","76 555 44 33",admin);
        addPatient("Ka","Mamadou Lamine",1960,1,7,"M","77 666 77 88","mlamine.ka@email.sn",
                "Saint-Louis, Sor","B+","Sulfamides","Cardiopathie","Adja Ka","77 444 33 22",admin);
        addPatient("Gueye","Binta",2022,11,20,"F","77 888 99 00","binta.gueye@email.sn",
                "Dakar, Sicap Liberté","A+","Lait maternel","Aucun","Fatou Diallo","77 123 45 67",admin);
        addPatient("Sow","Omar",2018,3,10,"M","78 999 00 11","omar.sow@email.sn",
                "Dakar, Mermoz","O+","Arachide","Aucun","Aïssatou Sow","78 234 56 78",admin);
        addPatient("Diop","Aminata",2010,7,5,"F","76 111 22 33","aminata.diop2@email.sn",
                "Dakar, Grand Dakar","B+","Aucune","Aucun","Mamadou Diop","77 111 22 33",admin);
    }

    private void addPatient(String nom, String prenom, int an, int m, int j, String sexe,
                             String tel, String email, String adresse, String gs, String allergies,
                             String antecedents, String contactNom, String contactTel, User admin) {
        if (email != null && !email.isBlank() &&
            patientRepository.findAll().stream().anyMatch(p -> email.equalsIgnoreCase(p.getEmail()))) {
            log.info("✓ Patient déjà présent : {} {}", prenom, nom); return;
        }
        patientRepository.save(Patient.builder().nom(nom).prenom(prenom)
                .dateNaissance(LocalDate.of(an, m, j)).sexe(sexe).telephone(tel).email(email)
                .adresse(adresse).groupeSanguin(gs).allergies(allergies).antecedents(antecedents)
                .personneContactNom(contactNom).personneContactTelephone(contactTel).createdBy(admin).build());
        log.info("✓ Patient créé : {} {} - {}", prenom, nom, email);
    }

    // ══════════════════════════════════════════════
    // PATIENTS - Vague 2
    // ══════════════════════════════════════════════

    private void seedMorePatients(User admin) {
        if (patientRepository.count() >= 28) { log.info("✓ Vague 2 patients déjà présente (≥28)"); return; }
        addPatient("Bâ","Aminata",1988,3,15,"F","77 101 20 30","amina.ba@email.sn",
                "Dakar, Fann","A+","Aucune","Aucun","Moussa Bâ","77 909 80 70",admin);
        addPatient("Dramé","Ousmane",1975,9,28,"M","78 202 30 40","ousmane.drame@email.sn",
                "Dakar, Parcelles Assainies","B+","Aucune","Hypertension, Diabète","Fatou Dramé","76 808 70 60",admin);
        addPatient("Kane","Rokhaya",1998,6,12,"F","76 303 40 50","rokhaya.kane@email.sn",
                "Pikine, Tally Boubess","O+","Poussière","Asthme","Mamadou Kane","77 707 60 50",admin);
        addPatient("Niang","Pape",1980,12,1,"M","77 404 50 60","pape.niang@email.sn",
                "Dakar, Médina","AB+","Aucune","Lombalgie chronique","Awa Niang","78 606 50 40",admin);
        addPatient("Mbaye","Coumba",1993,4,22,"F","78 505 60 70","coumba.mbaye@email.sn",
                "Guédiawaye, Golf Sud","A-","Pénicilline","Aucun","Ibrahima Mbaye","76 505 40 30",admin);
        addPatient("Seck","Adama",1972,8,5,"M","77 606 70 80","adama.seck@email.sn",
                "Dakar, Yoff","B-","Ibuprofène","Arthrose","Marième Seck","77 404 30 20",admin);
        addPatient("Gaye","Aïda",2005,2,14,"F","78 707 80 90","aida.gaye@email.sn",
                "Dakar, Ngor","O-","Arachide","Aucun","Baye Gaye","76 303 20 10",admin);
        addPatient("Dème","Moustapha",1997,10,30,"M","76 808 90 01","moustapha.deme@email.sn",
                "Thiaroye, Cité","AB-","Aucune","Migraine","Khadija Dème","77 202 10 00",admin);
        addPatient("Ly","Ramatoulaye",1983,7,18,"F","77 909 01 12","ramatou.ly@email.sn",
                "Dakar, Biscuiterie","A+","Sulfamides","Anémie","Ousmane Ly","78 101 90 80",admin);
        addPatient("Wade","Babacar",1968,11,3,"M","78 010 12 23","babacar.wade@email.sn",
                "Dakar, Liberté 6","O+","Aucune","Cardiopathie","Ndèye Wade","76 090 80 70",admin);
        addPatient("Sène","Fatimata",2007,5,25,"F","76 121 23 34","fatimata.sene@email.sn",
                "Rufisque, Avenues","B+","Aucune","Aucun","Mamadou Sène","77 080 70 60",admin);
        addPatient("Diakhoumpa","Ibrahima",1958,3,8,"M","77 232 34 45","ibrahima.diakh@email.sn",
                "Dakar, Hann Bel-Air","A+","Pénicilline","Diabète type 2, Rétinopathie","Mariam Diakhoumpa","78 070 60 50",admin);
        addPatient("Tamba","Mariétou",1990,9,17,"F","78 343 45 56","marietou.tamba@email.sn",
                "Dakar, Sotrac Mermoz","O+","Aucune","Aucun","Aliou Tamba","76 060 50 40",admin);
        addPatient("Ndour","El Hadji",1979,1,20,"M","77 454 56 67","elhadji.ndour@email.sn",
                "Dakar, Grand Médine","B-","Aucune","Hypertension","Aïssatou Ndour","77 050 40 30",admin);
    }

    // ══════════════════════════════════════════════
    // RENDEZ-VOUS - Vague 1
    // ══════════════════════════════════════════════

    private void seedRendezVous(List<Patient> patients, List<Medecin> medecins, User admin) {
        if (rendezVousRepository.count() >= 15) { log.info("✓ Rendez-vous déjà présents (≥15)"); return; }
        LocalDate today = LocalDate.now();
        String[][] data = {
            {"0","0","-7","9","0","TERMINE","Consultation cardiaque de routine","101"},
            {"1","1","-6","10","30","TERMINE","Consultation pédiatrique","102"},
            {"2","2","-5","14","0","TERMINE","Consultation gynécologique","103"},
            {"3","3","-4","8","30","TERMINE","Examen ophtalmologique","104"},
            {"4","4","-3","11","0","TERMINE","Suivi dermatologique","105"},
            {"5","5","-2","15","0","ANNULE","Annulé par le patient","101"},
            {"6","6","-1","9","30","TERMINE","Bilan général de santé","102"},
            {"0","1","-1","14","0","ABSENT","Patient non venu","103"},
            {"7","0","0","8","30","EN_COURS","Consultation cardiologique urgente","101"},
            {"8","2","0","9","0","CONFIRME","Suivi gynécologique","102"},
            {"9","4","0","10","0","CONFIRME","Examen de la peau","103"},
            {"2","6","0","11","0","EN_ATTENTE","Suivi neurologique","104"},
            {"1","7","0","14","30","EN_ATTENTE","Consultation générale","105"},
            {"5","3","0","15","0","CONFIRME","Contrôle ophtalmologique","101"},
            {"3","0","1","9","0","CONFIRME","Électrocardiogramme","102"},
            {"4","5","1","10","30","CONFIRME","Consultation dermatologique","103"},
            {"7","1","1","14","0","EN_ATTENTE","Vaccination enfant","104"},
            {"8","2","2","8","0","CONFIRME","Échographie","105"},
            {"9","4","3","11","0","EN_ATTENTE","Traitement acné","101"},
            {"6","6","5","9","0","EN_ATTENTE","Bilan neurologique complet","102"},
        };
        int created = 0;
        for (String[] d : data) {
            try {
                int pi = Integer.parseInt(d[0]), mi = Integer.parseInt(d[1]), doff = Integer.parseInt(d[2]);
                int h = Integer.parseInt(d[3]), min = Integer.parseInt(d[4]);
                if (pi >= patients.size() || mi >= medecins.size()) continue;
                LocalDateTime dt = today.atTime(h, min).plusDays(doff);
                List<RendezVous> existants = rendezVousRepository.findRendezVousBetween(dt.minusHours(1), dt.plusHours(1));
                boolean deja = existants.stream().anyMatch(r -> r.getPatient().getId().equals(patients.get(pi).getId())
                        && r.getMedecin().getId().equals(medecins.get(mi).getId()) && r.getDateHeure().equals(dt));
                if (deja) continue;
                rendezVousRepository.save(RendezVous.builder().patient(patients.get(pi)).medecin(medecins.get(mi))
                        .dateHeure(dt).dureeMinutes(30).statut(StatutRendezVous.valueOf(d[5]))
                        .motif(d[6]).salle(d[7]).createdBy(admin).build());
                created++;
            } catch (Exception e) { log.warn("⚠ Erreur RV: {}", e.getMessage()); }
        }
        log.info("✓ {} rendez-vous créés (vague 1)", created);
    }

    // ══════════════════════════════════════════════
    // RENDEZ-VOUS - Vague 2
    // ══════════════════════════════════════════════

    private void seedMoreRendezVous(List<Patient> patients, List<Medecin> medecins, User admin) {
        if (rendezVousRepository.count() >= 35) { log.info("✓ Vague 2 RV déjà présente (≥35)"); return; }
        LocalDate today = LocalDate.now();
        String[][] data = {
            // Anciens patients revisitent
            {"10","0","-10","10","0","TERMINE","Consultation de contrôle cardiaque","101"},
            {"11","1","-9","9","30","TERMINE","Suivi pédiatrique régulier","102"},
            {"12","2","-8","11","0","TERMINE","Consultation pré-natale","103"},
            {"0","3","-6","15","0","TERMINE","Contrôle vue","104"},
            {"3","4","-5","14","0","TERMINE","Traitement acné suivi","105"},
            {"2","5","-4","8","0","TERMINE","Consultation dermatologique","101"},
            // Nouveaux patients
            {"14","1","0","9","30","EN_ATTENTE","Consultation générale","102"},
            {"15","6","0","11","0","EN_ATTENTE","Bilan neurologique","103"},
            {"16","1","0","15","0","CONFIRME","Suivi enfant","104"},
            {"17","7","1","8","30","CONFIRME","Consultation générale","105"},
            {"18","0","1","10","0","CONFIRME","Électrocardiogramme","101"},
            {"19","2","2","9","0","CONFIRME","Échographie","102"},
            {"20","3","2","14","0","EN_ATTENTE","Examen des yeux","103"},
            {"21","5","3","9","0","EN_ATTENTE","Consultation peau","104"},
            {"22","1","3","11","0","EN_ATTENTE","Vaccination","105"},
        };
        int created = 0;
        for (String[] d : data) {
            try {
                int pi = Integer.parseInt(d[0]), mi = Integer.parseInt(d[1]), doff = Integer.parseInt(d[2]);
                int h = Integer.parseInt(d[3]), min = Integer.parseInt(d[4]);
                if (pi >= patients.size() || mi >= medecins.size()) continue;
                LocalDateTime dt = today.atTime(h, min).plusDays(doff);
                rendezVousRepository.save(RendezVous.builder().patient(patients.get(pi)).medecin(medecins.get(mi))
                        .dateHeure(dt).dureeMinutes(30).statut(StatutRendezVous.valueOf(d[5]))
                        .motif(d[6]).salle(d[7]).createdBy(admin).build());
                created++;
            } catch (Exception e) { log.warn("⚠ Erreur RV: {}", e.getMessage()); }
        }
        log.info("✓ {} rendez-vous créés (vague 2)", created);
    }

    // ══════════════════════════════════════════════
    // FACTURES - Vague 1
    // ══════════════════════════════════════════════

    private void seedFactures(List<Patient> patients, List<Medecin> medecins, User admin) {
        if (factureRepository.count() >= 12) { log.info("✓ Factures déjà présentes (≥12)"); return; }
        LocalDate today = LocalDate.now();
        addFacture(patients, medecins, admin, 0, 0, -7, 50000, 50000, "SOLDE");
        addFacture(patients, medecins, admin, 1, 1, -6, 35000, 35000, "SOLDE");
        addFacture(patients, medecins, admin, 2, 2, -5, 45000, 45000, "SOLDE");
        addFacture(patients, medecins, admin, 3, 3, -4, 40000, 20000, "PARTIELLEMENT_PAYE");
        addFacture(patients, medecins, admin, 4, 4, -3, 30000, 30000, "SOLDE");
        addFacture(patients, medecins, admin, 6, 6, -1, 25000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 7, 0, 0, 50000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 8, 2, 0, 45000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 9, 4, 0, 30000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 2, 6, 0, 55000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 1, 7, 0, 25000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 5, 3, 1, 40000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 3, 0, 1, 50000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 7, 1, 1, 35000, 0, "EN_ATTENTE");
        log.info("✓ 14 factures créées (vague 1)");
    }

    private void addFacture(List<Patient> patients, List<Medecin> medecins, User admin,
                             int pi, int mi, int doff, int total, int paye, String statut) {
        if (pi >= patients.size() || mi >= medecins.size()) return;
        LocalDate df = LocalDate.now().plusDays(doff);
        StatutPaiement sp = StatutPaiement.valueOf(statut);
        // Compteur monotone décalé : unique dans le run (count() s'incrémente à chaque save)
        // et au-dessus de tout numéro existant (décalage 100000) → aucune collision
        String numero = "FACT-" + (factureRepository.count() + 100000);
        factureRepository.save(Facture.builder()
                .numeroFacture(numero)
                .patient(patients.get(pi)).montantTotal(BigDecimal.valueOf(total))
                .montantPaye(BigDecimal.valueOf(paye)).statutPaiement(sp)
                .dateFacture(df).dateEcheance(df.plusDays(30))
                .modePaiement(sp == StatutPaiement.SOLDE ? "ESPECES" : null)
                .datePaiement(sp == StatutPaiement.SOLDE ? df.plusDays(1) : null)
                .notes("Consultation " + medecins.get(mi).getSpecialite()).createdBy(admin).build());
    }

    // ══════════════════════════════════════════════
    // FACTURES - Vague 2
    // ══════════════════════════════════════════════

    private void seedMoreFactures(List<Patient> patients, List<Medecin> medecins, User admin) {
        if (factureRepository.count() >= 25) { log.info("✓ Vague 2 factures déjà présente (≥25)"); return; }
        addFacture(patients, medecins, admin, 10, 0, -10, 50000, 50000, "SOLDE");
        addFacture(patients, medecins, admin, 11, 1, -9, 35000, 35000, "SOLDE");
        addFacture(patients, medecins, admin, 12, 2, -8, 45000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 0, 3, -6, 40000, 40000, "SOLDE");
        addFacture(patients, medecins, admin, 14, 1, 0, 25000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 15, 6, 0, 55000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 16, 1, 0, 35000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 17, 7, 1, 25000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 18, 0, 1, 50000, 0, "EN_ATTENTE");
        addFacture(patients, medecins, admin, 5, 0, -3, 30000, 30000, "SOLDE");
        addFacture(patients, medecins, admin, 9, 5, -2, 35000, 0, "EN_ATTENTE");
        log.info("✓ 11 factures créées (vague 2)");
    }

    // ══════════════════════════════════════════════
    // PRESCRIPTIONS - Vague 1
    // ══════════════════════════════════════════════

    private void seedPrescriptions(List<Patient> patients, List<Medecin> medecins) {
        if (prescriptionRepository.count() >= 8) { log.info("✓ Prescriptions déjà présentes (≥8)"); return; }
        LocalDate today = LocalDate.now();
        String[][] data = {
            {"0","0","-5","[{\"nom\":\"Amlodipine 5mg\",\"dosage\":\"5mg\",\"frequence\":\"1x/jour\",\"duree\":\"30 jours\",\"instructions\":\"À prendre le matin\"},{\"nom\":\"Aspirine 100mg\",\"dosage\":\"100mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"Après le repas\"}]","Prendre les médicaments régulièrement. Contrôle dans 1 mois.","Hypertension artérielle stade 1"},
            {"1","1","-4","[{\"nom\":\"Paracétamol 500mg\",\"dosage\":\"500mg\",\"frequence\":\"3x/jour\",\"duree\":\"5 jours\",\"instructions\":\"En cas de fièvre\"},{\"nom\":\"Amoxicilline 250mg\",\"dosage\":\"250mg\",\"frequence\":\"2x/jour\",\"duree\":\"7 jours\",\"instructions\":\"Après les repas\"}]","Traitement antibiotique à terminer absolument.","Infection respiratoire haute"},
            {"2","2","-3","[{\"nom\":\"Progestérone 200mg\",\"dosage\":\"200mg\",\"frequence\":\"1x/jour\",\"duree\":\"15 jours\",\"instructions\":\"Du 15e au 30e jour du cycle\"}]","Revoir dans 3 mois pour échographie de contrôle.","Troubles du cycle menstruel"},
            {"3","3","-2","[{\"nom\":\"Larmes artificielles\",\"dosage\":\"1 goutte\",\"frequence\":\"4x/jour\",\"duree\":\"30 jours\",\"instructions\":\"Instiller dans chaque œil\"},{\"nom\":\"Kétotifène 0.025%\",\"dosage\":\"1 goutte\",\"frequence\":\"2x/jour\",\"duree\":\"15 jours\",\"instructions\":\"Matin et soir\"}]","Éviter les écrans pendant les pauses.","Syndrome de l'œil sec"},
            {"4","4","-1","[{\"nom\":\"Crème corticostéroïde\",\"dosage\":\"Application locale\",\"frequence\":\"2x/jour\",\"duree\":\"10 jours\",\"instructions\":\"Couche fine sur les zones touchées\"}]","Hydrater la peau quotidiennement.","Eczéma atopique"},
            {"6","6","0","[{\"nom\":\"Diazépam 5mg\",\"dosage\":\"5mg\",\"frequence\":\"1x/jour\",\"duree\":\"15 jours\",\"instructions\":\"Au coucher\"},{\"nom\":\"Vitamine B1-B6-B12\",\"dosage\":\"Comprimé\",\"frequence\":\"2x/jour\",\"duree\":\"30 jours\",\"instructions\":\"Matin et midi\"}]","Suivi neurologique dans 2 semaines. Éviter l'alcool.","Névralgie intercostale"},
            {"7","1","1","[{\"nom\":\"Vitamine D 1000UI\",\"dosage\":\"1000UI\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"À prendre le matin\"}]","Exposition solaire modérée recommandée.","Carence en vitamine D"},
            {"8","2","2","[{\"nom\":\"Acide folique 5mg\",\"dosage\":\"5mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"Avant et pendant la grossesse\"},{\"nom\":\"Fer 200mg\",\"dosage\":\"200mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"À jeun avec du jus d'orange\"}]","Prise de sang de contrôle dans 1 mois.","Préparation pré-conceptionnelle"},
        };
        addPrescriptionBatch(patients, medecins, data);
        log.info("✓ 8 prescriptions créées (vague 1)");
    }

    // ══════════════════════════════════════════════
    // PRESCRIPTIONS - Vague 2
    // ══════════════════════════════════════════════

    private void seedMorePrescriptions(List<Patient> patients, List<Medecin> medecins) {
        if (prescriptionRepository.count() >= 15) { log.info("✓ Vague 2 prescriptions déjà présente (≥15)"); return; }
        LocalDate today = LocalDate.now();
        String[][] data = {
            {"5","0","-3","[{\"nom\":\"Atorvastatine 10mg\",\"dosage\":\"10mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"À prendre le soir\"},{\"nom\":\"Aspirine 75mg\",\"dosage\":\"75mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"Après le repas du midi\"}]","Bilan lipidique à contrôler dans 3 mois. Régime pauvre en graisses.","Hypercholestérolémie"},
            {"10","0","-8","[{\"nom\":\"Ramipril 5mg\",\"dosage\":\"5mg\",\"frequence\":\"1x/jour\",\"duree\":\"60 jours\",\"instructions\":\"À prendre le matin à jeun\"}]","TA à surveiller une fois par semaine.","Hypertension artérielle contrôlée"},
            {"11","1","-7","[{\"nom\":\"Vitamine C 500mg\",\"dosage\":\"500mg\",\"frequence\":\"1x/jour\",\"duree\":\"30 jours\",\"instructions\":\"À prendre le matin\"}]","Alimentation équilibrée recommandée.","Prévention carence hivernale"},
            {"16","1","0","[{\"nom\":\"Dafalgan 300mg enfant\",\"dosage\":\"300mg\",\"frequence\":\"3x/jour\",\"duree\":\"3 jours\",\"instructions\":\"En cas de fièvre >38.5°C\"},{\"nom\":\"Nurofène enfant 100mg\",\"dosage\":\"100mg\",\"frequence\":\"2x/jour\",\"duree\":\"3 jours\",\"instructions\":\"En cas de douleur\"}]","Repos pendant 48h. Réhydratation abondante.","Gastro-entérite aiguë"},
            {"12","1","-6","[{\"nom\":\"Amonide 1%\",\"dosage\":\"Application locale\",\"frequence\":\"2x/jour\",\"duree\":\"7 jours\",\"instructions\":\"Sur les plaques rouges uniquement\"}]","Éviter les produits parfumés. Crème hydratante matin et soir.","Dermite atopique"},
            {"18","0","1","[{\"nom\":\"Métoprolol 50mg\",\"dosage\":\"50mg\",\"frequence\":\"1x/jour\",\"duree\":\"90 jours\",\"instructions\":\"À prendre le matin\"},{\"nom\":\"Furosémide 40mg\",\"dosage\":\"40mg\",\"frequence\":\"1x/jour\",\"duree\":\"30 jours\",\"instructions\":\"À prendre le matin\"}]","Contrôle cardiaque dans 1 mois. Régime sans sel.","Insuffisance cardiaque légère"},
            {"23","4","-1","[{\"nom\":\"Photoprotecteur SPF50\",\"dosage\":\"Application large\",\"frequence\":\"2x/jour\",\"duree\":\"30 jours\",\"instructions\":\"Appliquer sur tout le corps avant exposition\"}]","Éviter l'exposition solaire entre 12h et 16h.","Prévention des lésions cutanées"},
        };
        addPrescriptionBatch(patients, medecins, data);
        log.info("✓ 7 prescriptions créées (vague 2)");
    }

    private void addPrescriptionBatch(List<Patient> patients, List<Medecin> medecins, String[][] data) {
        LocalDate today = LocalDate.now();
        int created = 0;
        for (String[] d : data) {
            try {
                int pi = Integer.parseInt(d[0]), mi = Integer.parseInt(d[1]), doff = Integer.parseInt(d[2]);
                if (pi >= patients.size() || mi >= medecins.size()) continue;
                prescriptionRepository.save(Prescription.builder().patient(patients.get(pi)).medecin(medecins.get(mi))
                        .datePrescription(today.plusDays(doff)).medicamentsJson(d[3]).instructions(d[4])
                        .valideJusqua(today.plusDays(doff).plusMonths(3)).diagnostic(d[5]).build());
                created++;
            } catch (Exception e) { log.warn("⚠ Erreur prescription: {}", e.getMessage()); }
        }
        log.info("✓ {} prescriptions créées", created);
    }

    // ══════════════════════════════════════════════
    // NOTIFICATIONS - Vague 1
    // ══════════════════════════════════════════════

    private void seedNotifications(List<User> users) {
        if (notificationRepository.count() >= 10) return;
        String[][] n = {
            {"RAPPEL","Rendez-vous dans 1 heure","Rappel : Consultation avec Dr. Diallo à 09h00","/rendez-vous"},
            {"INFO","Nouveau patient inscrit","Sophie Mendy a été ajoutée","/patients"},
            {"ALERTE","Facture en attente","FACT-1001 impayée depuis 30 jours","/factures"},
            {"SUCCES","Paiement reçu","50 000 FCFA reçu pour FACT-1002","/factures"},
            {"RAPPEL","Prescription à renouveler","Traitement de Mamadou Fall expire dans 7 jours","/prescriptions"},
            {"INFO","Planning modifié","Dr. Ba a modifié ses disponibilités","/planning"},
            {"ALERTE","Patient absent","Mamadou Fall non présent à 14h00","/rendez-vous"},
            {"SUCCES","Compte créé","Dr. Guela Lionel - Neurologue","/medecins"},
            {"INFO","Rapport disponible","Rapport d'activité mensuel prêt","/rapports"},
            {"RAPPEL","Stock bas","Paracétamol 500mg sous le seuil minimum","/pharmacie"},
        };
        for (String[] d : n) {
            User u = users.get(RANDOM.nextInt(users.size()));
            notificationRepository.save(Notification.builder().user(u).type(d[0]).titre(d[1])
                    .contenu(d[2]).lien(d[3]).lu(RANDOM.nextBoolean()).build());
        }
    }

    // ══════════════════════════════════════════════
    // NOTIFICATIONS - Vague 2
    // ══════════════════════════════════════════════

    private void seedMoreNotifications(List<User> users) {
        if (notificationRepository.count() >= 15) return;
        String[][] n = {
            {"RAPPEL","Nouveau RDV dans 2 jours","Rappel : Omar Sow chez Dr. Ba le 02/08","/rendez-vous"},
            {"INFO","Résultat d'analyse","Résultats d'analyse de Ibrahima Touré disponibles","/patients"},
            {"ALERTE","Échéance imminente","Facture FACT-1005 due dans 5 jours","/factures"},
            {"SUCCES","Dossier complété","Dossier de Fatimata Sène complété","/patients"},
            {"INFO","Nouveau médecin disponible","Dr. Ndeye Diop disponible cette semaine","/medecins"},
        };
        for (String[] d : n) {
            User u = users.get(RANDOM.nextInt(users.size()));
            notificationRepository.save(Notification.builder().user(u).type(d[0]).titre(d[1])
                    .contenu(d[2]).lien(d[3]).lu(RANDOM.nextBoolean()).build());
        }
    }

    // ══════════════════════════════════════════════
    // AUDIT LOGS - Vague 1
    // ══════════════════════════════════════════════

    private void seedAuditLogs(List<User> users, User admin) {
        if (auditLogRepository.count() >= 10) return;
        String[][] a = {
            {"CRÉATION","Patient","Création dossier : Sophie Mendy"},
            {"MODIFICATION","Patient","Mise à jour contact : Mamadou Fall"},
            {"CRÉATION","Rendez-vous","Dr. Diallo → Awa Dieng"},
            {"MODIFICATION","Rendez-vous","Annulation RDV du 25/07/2026"},
            {"CRÉATION","Facture","FACT-1001 pour Ibrahima Touré"},
            {"MODIFICATION","Facture","Paiement FACT-1002 enregistré"},
            {"CRÉATION","Prescription","Ordonnance Dr. Ba pour Khadidiatou Sy"},
            {"CRÉATION","Médecin","Ajout Dr. Alex Yangao"},
            {"SUPPRESSION","Rendez-vous","Suppression RDV en double"},
            {"CONNEXION","Système","Connexion admin - Jean-Marie KAREMAMANA"},
        };
        for (String[] d : a) {
            User u = users.get(RANDOM.nextInt(users.size()));
            auditLogRepository.save(AuditLog.builder().action(d[0]).ressource(d[1])
                    .user(u != admin ? u : admin).detail(d[2]).build());
        }
    }

    // ══════════════════════════════════════════════
    // AUDIT LOGS - Vague 2
    // ══════════════════════════════════════════════

    private void seedMoreAuditLogs(List<User> users, User admin) {
        if (auditLogRepository.count() >= 15) return;
        String[][] a = {
            {"CRÉATION","Patient","Création dossier : El Hadji Ndour"},
            {"MODIFICATION","Prescription","Renouvellement ordonnance : Aliou Sarr"},
            {"CRÉATION","Rendez-vous","Dr. Guela → Modou Faye"},
            {"MODIFICATION","Facture","Remise accordée sur FACT-1004"},
            {"CONNEXION","Système","Connexion Dr. Justin Kamanz"},
        };
        for (String[] d : a) {
            User u = users.get(RANDOM.nextInt(users.size()));
            auditLogRepository.save(AuditLog.builder().action(d[0]).ressource(d[1])
                    .user(u != admin ? u : admin).detail(d[2]).build());
        }
    }
}
