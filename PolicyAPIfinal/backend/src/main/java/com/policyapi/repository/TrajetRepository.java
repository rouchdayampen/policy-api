package com.policyapi.repository;

import com.policyapi.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Trajet
 * Contient les requêtes nécessaires pour les politiques de planification et réservation
 */
@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    /**
     * Politique: TrajetExiste(t)
     * Trouve les trajets planifiés
     */
    @Query("SELECT t FROM Trajet t WHERE t.statut = 'PLANIFIE'")
    List<Trajet> findTrajetsDisponibles();

    /**
     * Politique: DateTimeReservationValide(t)
     * Trouve les trajets avec date de départ future
     */
    @Query("SELECT t FROM Trajet t WHERE t.dateDepart > :maintenant AND t.statut = 'PLANIFIE'")
    List<Trajet> findTrajetsReservables(@Param("maintenant") LocalDateTime maintenant);

    /**
     * Trouve les trajets par agence d'origine
     */
    List<Trajet> findByAgenceOrigine(String agenceOrigine);

    /**
     * Trouve les trajets par agence de destination
     */
    List<Trajet> findByAgenceDestination(String agenceDestination);

    /**
     * Politique: EspaceDisponible(ad, h)
     * Compte les trajets arrivant à une agence à une heure donnée
     */
    @Query("SELECT COUNT(t) FROM Trajet t WHERE t.agenceDestination = :agence AND t.dateArrivee BETWEEN :debut AND :fin")
    Long countTrajetsArrivantPeriode(@Param("agence") String agence,
                                     @Param("debut") LocalDateTime debut,
                                     @Param("fin") LocalDateTime fin);

    /**
     * Trouve les trajets en cours
     */
    @Query("SELECT t FROM Trajet t WHERE t.statut = 'EN_COURS'")
    List<Trajet> findTrajetsEnCours();

    /**
     * Statistiques pour le dashboard
     */
    @Query("SELECT COUNT(t) FROM Trajet t WHERE t.statut = :statut")
    Long countByStatut(@Param("statut") Trajet.StatutTrajet statut);

    /**
     * Trouve les trajets d'un bus spécifique
     */
    @Query("SELECT t FROM Trajet t WHERE t.bus.id = :busId")
    List<Trajet> findTrajetsByBusId(@Param("busId") Long busId);

    /**
     * Trouve les trajets d'un chauffeur spécifique
     */
    @Query("SELECT t FROM Trajet t WHERE t.chauffeur.id = :chauffeurId")
    List<Trajet> findTrajetsByChauffeurId(@Param("chauffeurId") Long chauffeurId);
}