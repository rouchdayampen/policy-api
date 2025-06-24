package com.policyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO pour les requêtes de transfert entre agences
 * Utilisé dans la politique TRANSFERT_AGENCE
 */
public class TransfertRequest {

    @NotNull(message = "L'ID du bus est obligatoire")
    private Long busId;

    @NotBlank(message = "L'agence de destination est obligatoire")
    private String agenceDestination;

    @NotNull(message = "La date de transfert est obligatoire")
    private LocalDateTime dateTransfert;

    @NotNull(message = "L'ID du chauffeur est obligatoire")
    private Long chauffeurId;

    // Constructeurs
    public TransfertRequest() {}

    public TransfertRequest(Long busId, String agenceDestination, LocalDateTime dateTransfert, Long chauffeurId) {
        this.busId = busId;
        this.agenceDestination = agenceDestination;
        this.dateTransfert = dateTransfert;
        this.chauffeurId = chauffeurId;
    }

    // Getters et Setters
    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getAgenceDestination() { return agenceDestination; }
    public void setAgenceDestination(String agenceDestination) { this.agenceDestination = agenceDestination; }

    public LocalDateTime getDateTransfert() { return dateTransfert; }
    public void setDateTransfert(LocalDateTime dateTransfert) { this.dateTransfert = dateTransfert; }

    public Long getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(Long chauffeurId) { this.chauffeurId = chauffeurId; }
}