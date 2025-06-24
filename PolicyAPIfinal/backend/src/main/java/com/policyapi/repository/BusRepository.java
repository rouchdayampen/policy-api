package com.policyapi.repository;

import com.policyapi.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Bus
 * Contient les requêtes nécessaires pour les politiques mathématiques
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    /**
     * Politique: BusDisponible(a0, b, t, h)
     * Trouve les bus disponibles dans une agence donnée et d'un type spécifique
     */
    @Query("SELECT b FROM Bus b WHERE b.agenceActuelle = :agence AND b.type = :type AND b.statut = 'DISPONIBLE'")
    List<Bus> findBusDisponiblesParAgenceEtType(@Param("agence") String agence, @Param("type") Bus.TypeBus type);

    /**
     * Politique: EtatCritique(b)
     * Trouve les bus nécessitant une maintenance
     */
    @Query("SELECT b FROM Bus b WHERE b.statut IN ('MAINTENANCE', 'HORS_SERVICE')")
    List<Bus> findBusEnEtatCritique();

    /**
     * Trouve tous les bus d'une agence spécifique
     */
    List<Bus> findByAgenceActuelle(String agence);

    /**
     * Trouve un bus par son immatriculation
     */
    Optional<Bus> findByImmatriculation(String immatriculation);

    /**
     * Politique: CapaciteBus(b, d)
     * Compte les bus ayant de la capacité disponible
     */
    @Query("SELECT b FROM Bus b WHERE b.passagersActuels < b.capacite AND b.statut = 'DISPONIBLE'")
    List<Bus> findBusAvecCapaciteDisponible();

    /**
     * Statistiques pour le dashboard
     */
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.statut = :statut")
    Long countByStatut(@Param("statut") Bus.StatutBus statut);

    /**
     * Trouve les bus par type
     */
    List<Bus> findByType(Bus.TypeBus type);
}