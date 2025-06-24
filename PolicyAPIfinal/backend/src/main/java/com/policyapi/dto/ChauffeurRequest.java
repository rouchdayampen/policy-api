package com.policyapi.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO pour les requêtes d'affectation de chauffeur
 * Utilisé dans la politique AFFECTATION_CHAUFFEUR
 */
public class ChauffeurRequest {

    @NotNull(message = "L'ID du chauffeur est obligatoire")
    private Long chauffeurId;

    @NotNull(message = "La date d'affectation est obligatoire")
    private LocalDateTime dateAffectation;

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long trajetId;

    // Constructeurs
    public ChauffeurRequest() {}

    public ChauffeurRequest(Long chauffeurId, LocalDateTime dateAffectation, Long trajetId) {
        this.chauffeurId = chauffeurId;
        this.dateAffectation = dateAffectation;
        this.trajetId = trajetId;
    }

    // Getters et Setters
    public Long getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(Long chauffeurId) { this.chauffeurId = chauffeurId; }

    public LocalDateTime getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(LocalDateTime dateAffectation) { this.dateAffectation = dateAffectation; }

    public Long getTrajetId() { return trajetId; }
    public void setTrajetId(Long trajetId) { this.trajetId = trajetId; }
}