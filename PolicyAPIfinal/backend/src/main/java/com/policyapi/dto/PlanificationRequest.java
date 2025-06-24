package com.policyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO pour les requêtes de planification de trajet
 * Utilisé dans la politique PLANIFICATION_TRAJET
 */
public class PlanificationRequest {

    @NotBlank(message = "L'agence d'origine est obligatoire")
    private String agenceOrigine;

    @NotBlank(message = "L'agence de destination est obligatoire")
    private String agenceDestination;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDateTime dateDepart;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDateTime dateArrivee;

    @NotNull(message = "L'ID du bus est obligatoire")
    private Long busId;

    @NotNull(message = "L'ID du chauffeur est obligatoire")
    private Long chauffeurId;

    @NotNull(message = "Le prix est obligatoire")
    private Double prix;

    // Constructeurs
    public PlanificationRequest() {}

    public PlanificationRequest(String agenceOrigine, String agenceDestination,
                                LocalDateTime dateDepart, LocalDateTime dateArrivee,
                                Long busId, Long chauffeurId, Double prix) {
        this.agenceOrigine = agenceOrigine;
        this.agenceDestination = agenceDestination;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.busId = busId;
        this.chauffeurId = chauffeurId;
        this.prix = prix;
    }

    // Getters et Setters
    public String getAgenceOrigine() { return agenceOrigine; }
    public void setAgenceOrigine(String agenceOrigine) { this.agenceOrigine = agenceOrigine; }

    public String getAgenceDestination() { return agenceDestination; }
    public void setAgenceDestination(String agenceDestination) { this.agenceDestination = agenceDestination; }

    public LocalDateTime getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    public LocalDateTime getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDateTime dateArrivee) { this.dateArrivee = dateArrivee; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public Long getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(Long chauffeurId) { this.chauffeurId = chauffeurId; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
}