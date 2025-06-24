package com.policyapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entité Chauffeur - Représente un conducteur de bus
 * Utilisée dans les politiques d'affectation et de départ
 */
@Entity
@Table(name = "chauffeur")
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le permis de conduire est obligatoire")
    @Column(unique = true)
    private String numeroPermis;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutChauffeur statut;

    @NotBlank(message = "L'agence d'affectation est obligatoire")
    private String agenceActuelle;

    private LocalDateTime dernierTrajet;

    private Integer heuresTravaillees = 0;

    // Constructeurs
    public Chauffeur() {}

    public Chauffeur(String nom, String prenom, String numeroPermis, StatutChauffeur statut, String agenceActuelle) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroPermis = numeroPermis;
        this.statut = statut;
        this.agenceActuelle = agenceActuelle;
    }

    // Enum pour le statut du chauffeur
    public enum StatutChauffeur {
        DISPONIBLE("Disponible"),
        EN_SERVICE("En Service"),
        REPOS("En Repos"),
        CONGE("En Congé"),
        INDISPONIBLE("Indisponible");

        private final String description;

        StatutChauffeur(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes utilitaires pour les politiques mathématiques
    public boolean estDisponible() {
        return this.statut == StatutChauffeur.DISPONIBLE;
    }

    public boolean peutConduire() {
        return this.statut == StatutChauffeur.DISPONIBLE && this.heuresTravaillees < 8;
    }

    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNumeroPermis() { return numeroPermis; }
    public void setNumeroPermis(String numeroPermis) { this.numeroPermis = numeroPermis; }

    public StatutChauffeur getStatut() { return statut; }
    public void setStatut(StatutChauffeur statut) { this.statut = statut; }

    public String getAgenceActuelle() { return agenceActuelle; }
    public void setAgenceActuelle(String agenceActuelle) { this.agenceActuelle = agenceActuelle; }

    public LocalDateTime getDernierTrajet() { return dernierTrajet; }
    public void setDernierTrajet(LocalDateTime dernierTrajet) { this.dernierTrajet = dernierTrajet; }

    public Integer getHeuresTravaillees() { return heuresTravaillees; }
    public void setHeuresTravaillees(Integer heuresTravaillees) { this.heuresTravaillees = heuresTravaillees; }
}