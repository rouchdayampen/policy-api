package com.policyapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entité Utilisateur - Représente un client de l'agence
 * Utilisée dans les politiques de réservation
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotNull(message = "Le type de client est obligatoire")
    @Enumerated(EnumType.STRING)
    private TypeClient typeClient;

    private Double soldeCompte = 0.0;

    private Integer nombreVoyages = 0;

    // Constructeurs
    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String telephone, TypeClient typeClient) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.typeClient = typeClient;
    }

    // Enum pour le type de client
    public enum TypeClient {
        VIP("Client VIP"),
        REGULIER("Client Régulier"),
        OCCASIONNEL("Client Occasionnel");

        private final String description;

        TypeClient(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes utilitaires pour les politiques mathématiques
    public boolean peutReserver(Double prixBillet) {
        return this.soldeCompte >= prixBillet;
    }

    public boolean estClientFidele() {
        return this.nombreVoyages > 10 || this.typeClient == TypeClient.VIP;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public TypeClient getTypeClient() { return typeClient; }
    public void setTypeClient(TypeClient typeClient) { this.typeClient = typeClient; }

    public Double getSoldeCompte() { return soldeCompte; }
    public void setSoldeCompte(Double soldeCompte) { this.soldeCompte = soldeCompte; }

    public Integer getNombreVoyages() { return nombreVoyages; }
    public void setNombreVoyages(Integer nombreVoyages) { this.nombreVoyages = nombreVoyages; }
}