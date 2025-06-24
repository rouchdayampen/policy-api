package com.policyapi.controller;

import com.policyapi.dto.*;
import com.policyapi.entity.*;
import com.policyapi.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST principal pour l'API PolicyAPI
 * Expose les endpoints pour tester les politiques mathématiques
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * Endpoint de santé de l'API
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "PolicyAPI - Modélisation Mathématique des Politiques",
                "location", "Yaoundé - Cameroun",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * POLITIQUE PLANIFICATION_TRAJET
     * POST /api/policies/planification
     */
    @PostMapping("/policies/planification")
    public ResponseEntity<Map<String, Object>> evaluerPlanification(@Valid @RequestBody PlanificationRequest request) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiquePlanification(request);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * POLITIQUE RESERVATION
     * POST /api/policies/reservation
     */
    @PostMapping("/policies/reservation")
    public ResponseEntity<Map<String, Object>> evaluerReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiqueReservation(request);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * POLITIQUE AFFECTATION_CHAUFFEUR
     * POST /api/policies/affectation-chauffeur
     */
    @PostMapping("/policies/affectation-chauffeur")
    public ResponseEntity<Map<String, Object>> evaluerAffectationChauffeur(@Valid @RequestBody ChauffeurRequest request) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiqueAffectationChauffeur(request);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * POLITIQUE DEPART_BUS
     * POST /api/policies/depart-bus/{trajetId}
     */
    @PostMapping("/policies/depart-bus/{trajetId}")
    public ResponseEntity<Map<String, Object>> evaluerDepartBus(@PathVariable Long trajetId) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiqueDepartBus(trajetId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * POLITIQUE TRANSFERT_AGENCE
     * POST /api/policies/transfert-agence
     */
    @PostMapping("/policies/transfert-agence")
    public ResponseEntity<Map<String, Object>> evaluerTransfertAgence(@Valid @RequestBody TransfertRequest request) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiqueTransfertAgence(request);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * POLITIQUE MAINTENANCE
     * POST /api/policies/maintenance/{busId}
     */
    @PostMapping("/policies/maintenance/{busId}")
    public ResponseEntity<Map<String, Object>> evaluerMaintenance(@PathVariable Long busId) {
        try {
            Map<String, Object> resultat = policyService.evaluerPolitiqueMaintenance(busId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * STATISTIQUES GÉNÉRALES
     * GET /api/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        try {
            Map<String, Object> stats = policyService.getStatistiquesGenerales();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }

    /**
     * ENDPOINTS POUR RÉCUPÉRER LES DONNÉES
     */

    @GetMapping("/bus")
    public ResponseEntity<List<Bus>> getAllBus() {
        return ResponseEntity.ok(policyService.getAllBus());
    }

    @GetMapping("/chauffeurs")
    public ResponseEntity<List<Chauffeur>> getAllChauffeurs() {
        return ResponseEntity.ok(policyService.getAllChauffeurs());
    }

    @GetMapping("/trajets")
    public ResponseEntity<List<Trajet>> getAllTrajets() {
        return ResponseEntity.ok(policyService.getAllTrajets());
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseEntity.ok(policyService.getAllUtilisateurs());
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(policyService.getAllReservations());
    }
}