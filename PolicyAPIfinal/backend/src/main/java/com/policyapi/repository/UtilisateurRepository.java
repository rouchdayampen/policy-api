package com.policyapi.repository;

import com.policyapi.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 * Contient les requêtes nécessaires pour les politiques de réservation
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son email
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Trouve un utilisateur par son téléphone
     */
    Optional<Utilisateur> findByTelephone(String telephone);

    /**
     * Politique: PaiementEffectue(u)
     * Trouve les utilisateurs ayant un solde suffisant
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.soldeCompte >= :montantMinimum")
    List<Utilisateur> findUtilisateursAvecSoldeSuffisant(@Param("montantMinimum") Double montantMinimum);

    /**
     * Trouve les clients VIP
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.typeClient = 'VIP'")
    List<Utilisateur> findClientsVip();

    /**
     * Trouve les clients fidèles (plus de 10 voyages)
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.nombreVoyages > 10")
    List<Utilisateur> findClientsFideles();

    /**
     * Statistiques pour le dashboard
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.typeClient = :type")
    Long countByTypeClient(@Param("type") Utilisateur.TypeClient type);

    /**
     * Calcule le solde total de tous les utilisateurs
     */
    @Query("SELECT SUM(u.soldeCompte) FROM Utilisateur u")
    Double calculateSoldeTotalUtilisateurs();
}