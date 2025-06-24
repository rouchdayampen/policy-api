package com.policyapi.repository;

import com.policyapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Reservation
 * Contient les requêtes nécessaires pour les politiques de réservation
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Trouve les réservations d'un utilisateur
     */
    List<Reservation> findByUtilisateurId(Long utilisateurId);

    /**
     * Trouve les réservations d'un trajet
     */
    List<Reservation> findByTrajetId(Long trajetId);

    /**
     * Trouve une réservation par son numéro
     */
    Optional<Reservation> findByNumeroReservation(String numeroReservation);

    /**
     * Politique: PlaceDisponible(b, t)
     * Calcule le nombre de places réservées pour un trajet
     */
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.trajet.id = :trajetId AND r.statut IN ('EN_ATTENTE', 'CONFIRMEE')")
    Integer calculatePlacesReserveesTrajet(@Param("trajetId") Long trajetId);

    /**
     * Trouve les réservations confirmées
     */
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'CONFIRMEE'")
    List<Reservation> findReservationsConfirmees();

    /**
     * Trouve les réservations en attente de paiement
     */
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'EN_ATTENTE'")
    List<Reservation> findReservationsEnAttente();

    /**
     * Statistiques pour le dashboard
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    Long countByStatut(@Param("statut") Reservation.StatutReservation statut);

    /**
     * Calcule le chiffre d'affaires total
     */
    @Query("SELECT SUM(r.montantTotal) FROM Reservation r WHERE r.statut = 'CONFIRMEE' AND r.datePaiement BETWEEN :debut AND :fin")
    Double calculateChiffreAffaires(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    /**
     * Trouve les réservations récentes
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateReservation >= :dateDebut ORDER BY r.dateReservation DESC")
    List<Reservation> findReservationsRecentes(@Param("dateDebut") LocalDateTime dateDebut);
}