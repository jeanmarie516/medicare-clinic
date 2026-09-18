package com.medicare.service;

import com.medicare.dto.ConfigurationDto;
import com.medicare.entity.Configuration;
import com.medicare.entity.User;
import com.medicare.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final AuditService auditService;

    // Clés de configuration par catégorie
    public static final String PREFIX_CLINIQUE = "clinique.";
    public static final String PREFIX_RDV = "rdv.";
    public static final String PREFIX_FACTURATION = "facturation.";
    public static final String PREFIX_NOTIF = "notification.";

    // Defaults
    private static final Map<String, String> DEFAULTS = new LinkedHashMap<>();

    static {
        // Infos clinique
        DEFAULTS.put("clinique.nom", "MediCare Clinique");
        DEFAULTS.put("clinique.adresse", "");
        DEFAULTS.put("clinique.telephone", "");
        DEFAULTS.put("clinique.email", "");
        DEFAULTS.put("clinique.logo", "");
        DEFAULTS.put("clinique.horaires", "08:00-18:00");

        // Rendez-vous
        DEFAULTS.put("rdv.duree_defaut", "30");
        DEFAULTS.put("rdv.duree_max", "120");
        DEFAULTS.put("rdv.rappel_heures", "24");
        DEFAULTS.put("rdv.annulation_heures", "24");
        DEFAULTS.put("rdv.salle_defaut", "");
        DEFAULTS.put("rdv.numero_rdv_auto", "true");

        // Facturation
        DEFAULTS.put("facturation.tva_defaut", "0");
        DEFAULTS.put("facturation.devise", "FCFA");
        DEFAULTS.put("facturation.numero_auto", "true");
        DEFAULTS.put("facturation.prefix", "FACT-");
        DEFAULTS.put("facturation.echeance_jours", "30");
        DEFAULTS.put("facturation.remise_auto", "false");

        // Notifications
        DEFAULTS.put("notification.email_active", "false");
        DEFAULTS.put("notification.rappel_rdv", "true");
        DEFAULTS.put("notification.facture_impayee", "true");
        DEFAULTS.put("notification.nouveau_rdv", "true");
        DEFAULTS.put("notification.sound", "true");
    }

    public ConfigurationService(ConfigurationRepository configurationRepository, AuditService auditService) {
        this.configurationRepository = configurationRepository;
        this.auditService = auditService;
    }

    /**
     * Récupérer toutes les configurations
     */
    public Map<String, String> getAll() {
        List<Configuration> configs = configurationRepository.findAll();
        Map<String, String> result = new LinkedHashMap<>(DEFAULTS);

        for (Configuration config : configs) {
            result.put(config.getCle(), config.getValeur());
        }

        return result;
    }

    /**
     * Récupérer les configurations par préfixe
     */
    public Map<String, String> getByPrefix(String prefix) {
        List<Configuration> configs = configurationRepository.findByCleStartingWith(prefix);
        Map<String, String> result = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        for (Configuration config : configs) {
            result.put(config.getCle(), config.getValeur());
        }

        return result;
    }

    /**
     * Récupérer une configuration par clé
     */
    public String getValue(String cle) {
        return configurationRepository.findByCle(cle)
                .map(Configuration::getValeur)
                .orElse(DEFAULTS.getOrDefault(cle, ""));
    }

    /**
     * Sauvegarder ou mettre à jour des configurations (batch)
     */
    @Transactional
    public ConfigurationDto saveAll(Map<String, String> newConfigs, User admin) {
        int updated = 0;
        int created = 0;

        for (Map.Entry<String, String> entry : newConfigs.entrySet()) {
            String cle = entry.getKey();
            String valeur = entry.getValue();

            if (cle == null || cle.isBlank() || valeur == null) continue;

            var existing = configurationRepository.findByCle(cle);
            if (existing.isPresent()) {
                Configuration config = existing.get();
                if (!config.getValeur().equals(valeur)) {
                    config.setValeur(valeur);
                    configurationRepository.save(config);
                    updated++;
                }
            } else {
                Configuration config = new Configuration(cle, valeur, null);
                configurationRepository.save(config);
                created++;
            }
        }

        String detail = "Configurations sauvegardées : " + created + " créées, " + updated + " mises à jour";
        auditService.log("CONFIGURATION", "SYSTEME", detail, admin);

        return new ConfigurationDto(newConfigs, detail);
    }

    /**
     * Réinitialiser les valeurs par défaut
     */
    @Transactional
    public ConfigurationDto resetDefaults(User admin) {
        configurationRepository.deleteAll();
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            configurationRepository.save(new Configuration(entry.getKey(), entry.getValue(), null));
        }

        auditService.log("RESET_CONFIGURATION", "SYSTEME", "Réinitialisation des configurations par défaut", admin);

        return new ConfigurationDto(getAll(), "Configurations réinitialisées par défaut");
    }
}
