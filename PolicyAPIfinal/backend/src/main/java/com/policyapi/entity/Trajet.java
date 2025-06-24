package com.policyapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entité Trajet - Représente un voyage planifié
 * Utilisée dans les politiques de planification et de réservation
 */
@Entity
@Table(name = "trajet")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'agence d'origine est obligatoire")
    private String agenceOrigine;

    @NotBlank(message = "L'agence de destination est obligatoire")
    private String agenceDestination;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDateTime dateDepart;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDateTime dateArrivee;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id")
    private Chauffeur chauffeur;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutTrajet statut;

    private Double prix;

    private Integer placesReservees = 0;

    // Constructeurs
    public Trajet() {}

    public Trajet(String agenceOrigine, String agenceDestination, LocalDateTime dateDepart,
                  LocalDateTime dateArrivee, StatutTrajet statut, Double prix) {
        this.agenceOrigine = agenceOrigine;
        this.agenceDestination = agenceDestination;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.statut = statut;
        this.prix = prix;
    }

    // Enum pour le statut du trajet
    public enum StatutTrajet {
        PLANIFIE("Planifié"),
        EN_COURS("En Cours"),
        TERMINE("Terminé"),
        ANNULE("Annulé");

        private final String description;

        StatutTrajet(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes utilitaires pour les politiques mathématiques
    public boolean estPlanifie() {
        return this.statut == StatutTrajet.PLANIFIE;
    }

    public boolean aPlacesDisponibles() {
        return this.bus != null && this.placesReservees < this.bus.getCapacite();
    }

    public boolean estValideReservation() {
        return this.statut == StatutTrajet.PLANIFIE &&
                this.dateDepart.isAfter(LocalDateTime.now()) &&
                aPlacesDisponibles();
    }

    public String getLibelleTrajet() {
        return this.agenceOrigine + " → " + this.agenceDestination;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAgenceOrigine() { return agenceOrigine; }
    public void setAgenceOrigine(String agenceOrigine) { this.agenceOrigine = agenceOrigine; }

    public String getAgenceDestination() { return agenceDestination; }
    public void setAgenceDestination(String agenceDestination) { this.agenceDestination = agenceDestination; }

    public LocalDateTime getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    public LocalDateTime getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDateTime dateArrivee) { this.dateArrivee = dateArrivee; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public Chauffeur getChauffeur() { return chauffeur; }
    public void setChauffeur(Chauffeur chauffeur) { this.chauffeur = chauffeur; }

    public StatutTrajet getStatut() { return statut; }
    public void setStatut(StatutTrajet statut) { this.statut = statut; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getPlacesReservees() { return placesReservees; }
    public void setPlacesReservees(Integer placesReservees) { this.placesReservees = placesReservees; }
}