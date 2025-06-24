package com.policyapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour les requêtes de réservation
 * Utilisé dans la politique RESERVATION
 */
public class ReservationRequest {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long trajetId;

    @Min(value = 1, message = "Le nombre de places doit être supérieur à 0")
    private Integer nombrePlaces;

    // Constructeurs
    public ReservationRequest() {}

    public ReservationRequest(Long utilisateurId, Long trajetId, Integer nombrePlaces) {
        this.utilisateurId = utilisateurId;
        this.trajetId = trajetId;
        this.nombrePlaces = nombrePlaces;
    }

    // Getters et Setters
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public Long getTrajetId() { return trajetId; }
    public void setTrajetId(Long trajetId) { this.trajetId = trajetId; }

    public Integer getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }
}