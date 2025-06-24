package com.policyapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entité Reservation - Représente une réservation de billet
 * Utilisée dans les politiques de réservation et de paiement
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'utilisateur est obligatoire")
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @NotNull(message = "Le trajet est obligatoire")
    @ManyToOne
    @JoinColumn(name = "trajet_id")
    private Trajet trajet;

    @Min(value = 1, message = "Le nombre de places doit être supérieur à 0")
    private Integer nombrePlaces;

    private Double montantTotal;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    private LocalDateTime dateReservation;

    private LocalDateTime datePaiement;

    private String numeroReservation;

    // Constructeurs
    public Reservation() {
        this.dateReservation = LocalDateTime.now();
        this.numeroReservation = generateNumeroReservation();
    }

    public Reservation(Utilisateur utilisateur, Trajet trajet, Integer nombrePlaces, Double montantTotal) {
        this();
        this.utilisateur = utilisateur;
        this.trajet = trajet;
        this.nombrePlaces = nombrePlaces;
        this.montantTotal = montantTotal;
        this.statut = StatutReservation.EN_ATTENTE;
    }

    // Enum pour le statut de la réservation
    public enum StatutReservation {
        EN_ATTENTE("En Attente de Paiement"),
        CONFIRMEE("Confirmée"),
        ANNULEE("Annulée"),
        UTILISEE("Utilisée");

        private final String description;

        StatutReservation(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes utilitaires
    private String generateNumeroReservation() {
        return "RES" + System.currentTimeMillis();
    }

    public boolean estConfirmee() {
        return this.statut == StatutReservation.CONFIRMEE;
    }

    public boolean peutEtreAnnulee() {
        return this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Trajet getTrajet() { return trajet; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }

    public Integer getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public String getNumeroReservation() { return numeroReservation; }
    public void setNumeroReservation(String numeroReservation) { this.numeroReservation = numeroReservation; }
}