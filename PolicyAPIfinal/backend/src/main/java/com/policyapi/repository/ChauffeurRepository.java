package com.policyapi.repository;

import com.policyapi.entity.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Chauffeur
 * Contient les requêtes nécessaires pour les politiques d'affectation
 */
@Repository
public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {

    /**
     * Politique: ChauffeurDisponible(a0, c)
     * Trouve les chauffeurs disponibles dans une agence donnée
     */
    @Query("SELECT c FROM Chauffeur c WHERE c.agenceActuelle = :agence AND c.statut = 'DISPONIBLE' AND c.heuresTravaillees < 8")
    List<Chauffeur> findChauffeursDisponiblesParAgence(@Param("agence") String agence);

    /**
     * Trouve tous les chauffeurs d'une agence
     */
    List<Chauffeur> findByAgenceActuelle(String agence);

    /**
     * Trouve un chauffeur par son numéro de permis
     */
    Optional<Chauffeur> findByNumeroPermis(String numeroPermis);

    /**
     * Politique: ChauffeurDisponible(c, a0, d)
     * Vérifie si un chauffeur spécifique est disponible
     */
    @Query("SELECT c FROM Chauffeur c WHERE c.id = :chauffeurId AND c.statut = 'DISPONIBLE' AND c.heuresTravaillees < 8")
    Optional<Chauffeur> findChauffeurDisponibleById(@Param("chauffeurId") Long chauffeurId);

    /**
     * Statistiques pour le dashboard
     */
    @Query("SELECT COUNT(c) FROM Chauffeur c WHERE c.statut = :statut")
    Long countByStatut(@Param("statut") Chauffeur.StatutChauffeur statut);

    /**
     * Trouve les chauffeurs en repos ou en congé
     */
    @Query("SELECT c FROM Chauffeur c WHERE c.statut IN ('REPOS', 'CONGE')")
    List<Chauffeur> findChauffeursEnRepos();
}