package com.policyapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entité Bus - Représente un véhicule de transport
 * Utilisée dans les politiques de planification, départ et transfert
 */
@Entity
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro d'immatriculation est obligatoire")
    @Column(unique = true)
    private String immatriculation;

    @NotNull(message = "Le type de bus est obligatoire")
    @Enumerated(EnumType.STRING)
    private TypeBus type;

    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private Integer capacite;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutBus statut;

    @NotBlank(message = "L'agence d'affectation est obligatoire")
    private String agenceActuelle;

    private Integer passagersActuels = 0;

    // Constructeurs
    public Bus() {}

    public Bus(String immatriculation, TypeBus type, Integer capacite, StatutBus statut, String agenceActuelle) {
        this.immatriculation = immatriculation;
        this.type = type;
        this.capacite = capacite;
        this.statut = statut;
        this.agenceActuelle = agenceActuelle;
    }

    // Enum pour le type de bus
    public enum TypeBus {
        VIP("VIP - Confort Premium"),
        CLASSIQUE("Classique - Standard");

        private final String description;

        TypeBus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Enum pour le statut du bus
    public enum StatutBus {
        DISPONIBLE("Disponible"),
        EN_ROUTE("En Route"),
        MAINTENANCE("En Maintenance"),
        HORS_SERVICE("Hors Service");

        private final String description;

        StatutBus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes utilitaires pour les politiques mathématiques
    public boolean estDisponible() {
        return this.statut == StatutBus.DISPONIBLE;
    }

    public boolean aCapaciteDisponible() {
        return this.passagersActuels < this.capacite;
    }

    public boolean estEnEtatCritique() {
        return this.statut == StatutBus.MAINTENANCE || this.statut == StatutBus.HORS_SERVICE;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public TypeBus getType() { return type; }
    public void setType(TypeBus type) { this.type = type; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    public StatutBus getStatut() { return statut; }
    public void setStatut(StatutBus statut) { this.statut = statut; }

    public String getAgenceActuelle() { return agenceActuelle; }
    public void setAgenceActuelle(String agenceActuelle) { this.agenceActuelle = agenceActuelle; }

    public Integer getPassagersActuels() { return passagersActuels; }
    public void setPassagersActuels(Integer passagersActuels) { this.passagersActuels = passagersActuels; }
}