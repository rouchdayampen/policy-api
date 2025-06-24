package com.policyapi.service;

import com.policyapi.dto.*;
import com.policyapi.entity.*;
import com.policyapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service principal contenant l'implémentation des politiques mathématiques
 * Chaque méthode correspond à une politique spécifique définie dans le rapport
 */
@Service
@Transactional
public class PolicyService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ChauffeurRepository chauffeurRepository;

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * POLITIQUE PLANIFICATION_TRAJET
     * Policy(PLANIFICATION_TRAJET, (t, h)) = ALLOW ⟺ 
     * TypeBus(b) ∧ EspaceDisponible(ad,h) ∧ CapaciteBus(b,d) ∧ BusDisponible(a0,b,t,h) ∧ ChauffeurDisponible(a0,c)
     */
    public Map<String, Object> evaluerPolitiquePlanification(PlanificationRequest request) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique PLANIFICATION_TRAJET:\n");

        try {
            // Récupération des entités
            Optional<Bus> busOpt = busRepository.findById(request.getBusId());
            Optional<Chauffeur> chauffeurOpt = chauffeurRepository.findById(request.getChauffeurId());

            if (busOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Bus inexistant\n");
            } else if (chauffeurOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Chauffeur inexistant\n");
            } else {
                Bus bus = busOpt.get();
                Chauffeur chauffeur = chauffeurOpt.get();

                // 1. Vérification TypeBus(b)
                explication.append("✅ TypeBus(b) = ").append(bus.getType()).append("\n");

                // 2. Vérification BusDisponible(a0,b,t,h)
                boolean busDisponible = bus.estDisponible() &&
                        bus.getAgenceActuelle().equals(request.getAgenceOrigine());
                if (busDisponible) {
                    explication.append("✅ BusDisponible(a0,b,t,h) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ BusDisponible(a0,b,t,h) = NON\n");
                }

                // 3. Vérification ChauffeurDisponible(a0,c)
                boolean chauffeurDisponible = chauffeur.estDisponible() &&
                        chauffeur.getAgenceActuelle().equals(request.getAgenceOrigine());
                if (chauffeurDisponible) {
                    explication.append("✅ ChauffeurDisponible(a0,c) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ ChauffeurDisponible(a0,c) = NON\n");
                }

                // 4. Vérification EspaceDisponible(ad,h)
                LocalDateTime debut = request.getDateArrivee().minusHours(1);
                LocalDateTime fin = request.getDateArrivee().plusHours(1);
                Long trajetsArrivant = trajetRepository.countTrajetsArrivantPeriode(
                        request.getAgenceDestination(), debut, fin);
                boolean espaceDisponible = trajetsArrivant < 5; // Maximum 5 bus par heure

                if (espaceDisponible) {
                    explication.append("✅ EspaceDisponible(ad,h) = OUI (").append(trajetsArrivant).append(" trajets)\n");
                } else {
                    autorise = false;
                    explication.append("❌ EspaceDisponible(ad,h) = NON (").append(trajetsArrivant).append(" trajets)\n");
                }

                // 5. Vérification CapaciteBus(b,d)
                boolean capaciteOk = bus.aCapaciteDisponible();
                if (capaciteOk) {
                    explication.append("✅ CapaciteBus(b,d) < MAX (").append(bus.getPassagersActuels())
                            .append("/").append(bus.getCapacite()).append(")\n");
                } else {
                    autorise = false;
                    explication.append("❌ CapaciteBus(b,d) >= MAX\n");
                }

                // Création du trajet si politique approuvée
                if (autorise) {
                    Trajet trajet = new Trajet(
                            request.getAgenceOrigine(),
                            request.getAgenceDestination(),
                            request.getDateDepart(),
                            request.getDateArrivee(),
                            Trajet.StatutTrajet.PLANIFIE,
                            request.getPrix()
                    );
                    trajet.setBus(bus);
                    trajet.setChauffeur(chauffeur);

                    trajetRepository.save(trajet);
                    explication.append("✅ Trajet créé avec succès (ID: ").append(trajet.getId()).append(")\n");
                    resultat.put("trajetId", trajet.getId());
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    /**
     * POLITIQUE RESERVATION
     * Policy(RESERVATION, r) = ALLOW ⟺ 
     * TrajetExiste(t) ∧ DateTimeReservationValide(t) ∧ PlaceDisponible(b,t) ∧ PaiementEffectue(u)
     */
    public Map<String, Object> evaluerPolitiqueReservation(ReservationRequest request) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique RESERVATION:\n");

        try {
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(request.getUtilisateurId());
            Optional<Trajet> trajetOpt = trajetRepository.findById(request.getTrajetId());

            if (utilisateurOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Utilisateur inexistant\n");
            } else if (trajetOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Trajet inexistant\n");
            } else {
                Utilisateur utilisateur = utilisateurOpt.get();
                Trajet trajet = trajetOpt.get();

                // 1. Vérification TrajetExiste(t)
                boolean trajetExiste = trajet.estPlanifie();
                if (trajetExiste) {
                    explication.append("✅ TrajetExiste(t) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ TrajetExiste(t) = NON (statut: ").append(trajet.getStatut()).append(")\n");
                }

                // 2. Vérification DateTimeReservationValide(t)
                boolean dateValide = trajet.getDateDepart().isAfter(LocalDateTime.now());
                if (dateValide) {
                    explication.append("✅ DateTimeReservationValide(t) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ DateTimeReservationValide(t) = NON\n");
                }

                // 3. Vérification PlaceDisponible(b,d)
                Integer placesReservees = reservationRepository.calculatePlacesReserveesTrajet(trajet.getId());
                Integer placesDisponibles = trajet.getBus().getCapacite() - placesReservees;
                boolean placeDisponible = placesDisponibles >= request.getNombrePlaces();

                if (placeDisponible) {
                    explication.append("✅ PlaceDisponible(b,d) = OUI (")
                            .append(placesDisponibles).append(" places disponibles)\n");
                } else {
                    autorise = false;
                    explication.append("❌ PlaceDisponible(b,d) = NON (")
                            .append(placesDisponibles).append(" places disponibles)\n");
                }

                // 4. Vérification PaiementEffectue(u)
                Double montantTotal = trajet.getPrix() * request.getNombrePlaces();
                boolean paiementOk = utilisateur.peutReserver(montantTotal);

                if (paiementOk) {
                    explication.append("✅ PaiementEffectue(u) = OUI (solde: ")
                            .append(utilisateur.getSoldeCompte()).append(" FCFA)\n");
                } else {
                    autorise = false;
                    explication.append("❌ PaiementEffectue(u) = NON (solde insuffisant)\n");
                }

                // Création de la réservation si politique approuvée
                if (autorise) {
                    Reservation reservation = new Reservation(utilisateur, trajet, request.getNombrePlaces(), montantTotal);
                    reservation.setStatut(Reservation.StatutReservation.CONFIRMEE);
                    reservation.setDatePaiement(LocalDateTime.now());

                    // Débit du compte utilisateur
                    utilisateur.setSoldeCompte(utilisateur.getSoldeCompte() - montantTotal);
                    utilisateur.setNombreVoyages(utilisateur.getNombreVoyages() + 1);

                    // Mise à jour du trajet
                    trajet.setPlacesReservees(trajet.getPlacesReservees() + request.getNombrePlaces());

                    reservationRepository.save(reservation);
                    utilisateurRepository.save(utilisateur);
                    trajetRepository.save(trajet);

                    explication.append("✅ Réservation créée (N°: ").append(reservation.getNumeroReservation()).append(")\n");
                    resultat.put("reservationId", reservation.getId());
                    resultat.put("numeroReservation", reservation.getNumeroReservation());
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    /**
     * POLITIQUE AFFECTATION_CHAUFFEUR
     * Policy(AFFECTATION_CHAUFFEUR,(c,d)) = ALLOW ⟺ ChauffeurDisponible(c,a0,d)
     */
    public Map<String, Object> evaluerPolitiqueAffectationChauffeur(ChauffeurRequest request) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique AFFECTATION_CHAUFFEUR:\n");

        try {
            Optional<Chauffeur> chauffeurOpt = chauffeurRepository.findById(request.getChauffeurId());
            Optional<Trajet> trajetOpt = trajetRepository.findById(request.getTrajetId());

            if (chauffeurOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Chauffeur inexistant\n");
            } else if (trajetOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Trajet inexistant\n");
            } else {
                Chauffeur chauffeur = chauffeurOpt.get();
                Trajet trajet = trajetOpt.get();

                // Vérification ChauffeurDisponible(c,a0,d)
                boolean chauffeurDisponible = chauffeur.estDisponible() &&
                        chauffeur.peutConduire() &&
                        chauffeur.getAgenceActuelle().equals(trajet.getAgenceOrigine());

                if (chauffeurDisponible) {
                    explication.append("✅ ChauffeurDisponible(c,a0,d) = OUI\n");
                    explication.append("  - Statut: ").append(chauffeur.getStatut()).append("\n");
                    explication.append("  - Heures travaillées: ").append(chauffeur.getHeuresTravaillees()).append("/8\n");
                    explication.append("  - Agence: ").append(chauffeur.getAgenceActuelle()).append("\n");

                    // Affectation du chauffeur
                    trajet.setChauffeur(chauffeur);
                    chauffeur.setStatut(Chauffeur.StatutChauffeur.EN_SERVICE);
                    chauffeur.setDernierTrajet(request.getDateAffectation());

                    trajetRepository.save(trajet);
                    chauffeurRepository.save(chauffeur);

                    explication.append("✅ Chauffeur affecté au trajet\n");
                } else {
                    autorise = false;
                    explication.append("❌ ChauffeurDisponible(c,a0,d) = NON\n");
                    explication.append("  - Statut: ").append(chauffeur.getStatut()).append("\n");
                    explication.append("  - Heures travaillées: ").append(chauffeur.getHeuresTravaillees()).append("/8\n");
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    /**
     * POLITIQUE DEPART_BUS
     * Différenciation VIP/Classique selon le type de bus
     */
    public Map<String, Object> evaluerPolitiqueDepartBus(Long trajetId) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique DEPART_BUS:\n");

        try {
            Optional<Trajet> trajetOpt = trajetRepository.findById(trajetId);

            if (trajetOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Trajet inexistant\n");
            } else {
                Trajet trajet = trajetOpt.get();
                Bus bus = trajet.getBus();
                Chauffeur chauffeur = trajet.getChauffeur();

                if (bus == null) {
                    autorise = false;
                    explication.append("❌ Aucun bus affecté\n");
                } else if (chauffeur == null) {
                    autorise = false;
                    explication.append("❌ Aucun chauffeur affecté\n");
                } else {
                    // Politique différente selon le type de bus
                    if (bus.getType() == Bus.TypeBus.VIP) {
                        // Bus VIP: peut partir avec au moins 1 passager
                        boolean peutPartir = trajet.getPlacesReservees() >= 1;
                        if (peutPartir) {
                            explication.append("✅ Bus VIP - Départ autorisé avec ")
                                    .append(trajet.getPlacesReservees()).append(" passager(s)\n");
                        } else {
                            autorise = false;
                            explication.append("❌ Bus VIP - Aucun passager réservé\n");
                        }
                    } else {
                        // Bus Classique: doit avoir au moins 50% de remplissage
                        int seuilMinimum = (int) Math.ceil(bus.getCapacite() * 0.5);
                        boolean peutPartir = trajet.getPlacesReservees() >= seuilMinimum;
                        if (peutPartir) {
                            explication.append("✅ Bus Classique - Départ autorisé (")
                                    .append(trajet.getPlacesReservees()).append("/").append(seuilMinimum).append(" min)\n");
                        } else {
                            autorise = false;
                            explication.append("❌ Bus Classique - Remplissage insuffisant (")
                                    .append(trajet.getPlacesReservees()).append("/").append(seuilMinimum).append(" min)\n");
                        }
                    }

                    // Vérifications communes
                    boolean chauffeurPret = chauffeur.getStatut() == Chauffeur.StatutChauffeur.EN_SERVICE;
                    boolean busDisponible = bus.getStatut() == Bus.StatutBus.DISPONIBLE;

                    if (!chauffeurPret) {
                        autorise = false;
                        explication.append("❌ Chauffeur non prêt (").append(chauffeur.getStatut()).append(")\n");
                    }

                    if (!busDisponible) {
                        autorise = false;
                        explication.append("❌ Bus non disponible (").append(bus.getStatut()).append(")\n");
                    }

                    // Autorisation du départ
                    if (autorise) {
                        trajet.setStatut(Trajet.StatutTrajet.EN_COURS);
                        bus.setStatut(Bus.StatutBus.EN_ROUTE);
                        bus.setPassagersActuels(trajet.getPlacesReservees());

                        trajetRepository.save(trajet);
                        busRepository.save(bus);

                        explication.append("✅ Départ autorisé - Trajet en cours\n");
                    }
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    /**
     * POLITIQUE TRANSFERT_AGENCE
     * Policy(TRANSFERT_AGENCE, (b, adest, d)) = ALLOW ⟺ 
     * BusExiste(asrc, *, b, d) ∧ ChauffeurDisponible(asrc, c) ∧ (asrc ≠ adest) ∧ EspaceDisponible(adest, d)
     */
    public Map<String, Object> evaluerPolitiqueTransfertAgence(TransfertRequest request) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique TRANSFERT_AGENCE:\n");

        try {
            Optional<Bus> busOpt = busRepository.findById(request.getBusId());
            Optional<Chauffeur> chauffeurOpt = chauffeurRepository.findById(request.getChauffeurId());

            if (busOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Bus inexistant\n");
            } else if (chauffeurOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Chauffeur inexistant\n");
            } else {
                Bus bus = busOpt.get();
                Chauffeur chauffeur = chauffeurOpt.get();

                // 1. Vérification BusExiste(asrc, *, b, d)
                boolean busExiste = bus.estDisponible();
                if (busExiste) {
                    explication.append("✅ BusExiste(asrc, *, b, d) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ BusExiste(asrc, *, b, d) = NON (").append(bus.getStatut()).append(")\n");
                }

                // 2. Vérification ChauffeurDisponible(asrc, c)
                boolean chauffeurDisponible = chauffeur.estDisponible() &&
                        chauffeur.getAgenceActuelle().equals(bus.getAgenceActuelle());
                if (chauffeurDisponible) {
                    explication.append("✅ ChauffeurDisponible(asrc, c) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ ChauffeurDisponible(asrc, c) = NON\n");
                }

                // 3. Vérification (asrc ≠ adest)
                boolean agencesDifferentes = !bus.getAgenceActuelle().equals(request.getAgenceDestination());
                if (agencesDifferentes) {
                    explication.append("✅ (asrc ≠ adest) = OUI (")
                            .append(bus.getAgenceActuelle()).append(" → ")
                            .append(request.getAgenceDestination()).append(")\n");
                } else {
                    autorise = false;
                    explication.append("❌ (asrc ≠ adest) = NON (même agence)\n");
                }

                // 4. Vérification EspaceDisponible(adest, d)
                LocalDateTime debut = request.getDateTransfert().minusHours(1);
                LocalDateTime fin = request.getDateTransfert().plusHours(1);
                Long transfertsArrivant = trajetRepository.countTrajetsArrivantPeriode(
                        request.getAgenceDestination(), debut, fin);
                boolean espaceDisponible = transfertsArrivant < 3; // Maximum 3 transferts par heure

                if (espaceDisponible) {
                    explication.append("✅ EspaceDisponible(adest, d) = OUI\n");
                } else {
                    autorise = false;
                    explication.append("❌ EspaceDisponible(adest, d) = NON (trop de transferts)\n");
                }

                // Exécution du transfert
                if (autorise) {
                    bus.setAgenceActuelle(request.getAgenceDestination());
                    chauffeur.setAgenceActuelle(request.getAgenceDestination());
                    chauffeur.setStatut(Chauffeur.StatutChauffeur.EN_SERVICE);

                    busRepository.save(bus);
                    chauffeurRepository.save(chauffeur);

                    explication.append("✅ Transfert effectué avec succès\n");
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    /**
     * POLITIQUE MAINTENANCE
     * Policy(MAINTENANCE, b) = ALLOW ⟺ EtatCritique(b)
     */
    public Map<String, Object> evaluerPolitiqueMaintenance(Long busId) {
        Map<String, Object> resultat = new HashMap<>();
        boolean autorise = true;
        StringBuilder explication = new StringBuilder("Évaluation Politique MAINTENANCE:\n");

        try {
            Optional<Bus> busOpt = busRepository.findById(busId);

            if (busOpt.isEmpty()) {
                autorise = false;
                explication.append("❌ Bus inexistant\n");
            } else {
                Bus bus = busOpt.get();

                // Vérification EtatCritique(b)
                boolean etatCritique = bus.estEnEtatCritique() ||
                        bus.getStatut() == Bus.StatutBus.DISPONIBLE; // Maintenance préventive

                if (etatCritique || bus.getStatut() == Bus.StatutBus.DISPONIBLE) {
                    explication.append("✅ EtatCritique(b) = OUI (").append(bus.getStatut()).append(")\n");

                    // Mise en maintenance
                    bus.setStatut(Bus.StatutBus.MAINTENANCE);
                    bus.setPassagersActuels(0);
                    busRepository.save(bus);

                    explication.append("✅ Bus mis en maintenance\n");
                } else {
                    autorise = false;
                    explication.append("❌ EtatCritique(b) = NON - Bus en service\n");
                }
            }

        } catch (Exception e) {
            autorise = false;
            explication.append("❌ Erreur: ").append(e.getMessage());
        }

        resultat.put("decision", autorise ? "ALLOW" : "DENY");
        resultat.put("explication", explication.toString());
        resultat.put("timestamp", LocalDateTime.now());

        return resultat;
    }

    // Méthodes utilitaires pour le dashboard
    public Map<String, Object> getStatistiquesGenerales() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalBus", busRepository.count());
        stats.put("busDisponibles", busRepository.countByStatut(Bus.StatutBus.DISPONIBLE));
        stats.put("busEnRoute", busRepository.countByStatut(Bus.StatutBus.EN_ROUTE));
        stats.put("busMaintenance", busRepository.countByStatut(Bus.StatutBus.MAINTENANCE));

        stats.put("totalChauffeurs", chauffeurRepository.count());
        stats.put("chauffeursDisponibles", chauffeurRepository.countByStatut(Chauffeur.StatutChauffeur.DISPONIBLE));
        stats.put("chauffeursEnService", chauffeurRepository.countByStatut(Chauffeur.StatutChauffeur.EN_SERVICE));

        stats.put("totalTrajets", trajetRepository.count());
        stats.put("trajetsPlanifies", trajetRepository.countByStatut(Trajet.StatutTrajet.PLANIFIE));
        stats.put("trajetsEnCours", trajetRepository.countByStatut(Trajet.StatutTrajet.EN_COURS));

        stats.put("totalReservations", reservationRepository.count());
        stats.put("reservationsConfirmees", reservationRepository.countByStatut(Reservation.StatutReservation.CONFIRMEE));
        stats.put("reservationsEnAttente", reservationRepository.countByStatut(Reservation.StatutReservation.EN_ATTENTE));

        stats.put("totalUtilisateurs", utilisateurRepository.count());
        stats.put("clientsVip", utilisateurRepository.countByTypeClient(Utilisateur.TypeClient.VIP));

        return stats;
    }

    // Getters pour les entités (pour le frontend)
    public List<Bus> getAllBus() { return busRepository.findAll(); }
    public List<Chauffeur> getAllChauffeurs() { return chauffeurRepository.findAll(); }
    public List<Trajet> getAllTrajets() { return trajetRepository.findAll(); }
    public List<Utilisateur> getAllUtilisateurs() { return utilisateurRepository.findAll(); }
    public List<Reservation> getAllReservations() { return reservationRepository.findAll(); }
}