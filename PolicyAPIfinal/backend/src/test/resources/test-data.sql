-- test-data.sql
-- Ce script est conçu pour initialiser les données de test après que le schéma
-- ait été créé par Hibernate (via ddl-auto=create-drop) ou un script schema.sql séparé.
-- Il garantit un état de données propre et prévisible avant chaque exécution de test.

---
-- 1. Désactiver temporairement les contrôles de clés étrangères.
--    Ceci permet de vider les tables sans se soucier des dépendances.
---
SET FOREIGN_KEY_CHECKS = 0;

---
-- 2. Vider toutes les tables de données en utilisant TRUNCATE TABLE.
--    TRUNCATE TABLE est plus rapide que DELETE FROM et, pour MySQL InnoDB,
--    il réinitialise généralement les compteurs AUTO_INCREMENT par défaut.
--    L'ordre est du plus dépendant au moins dépendant pour une meilleure clarté et sécurité,
--    même si FOREIGN_KEY_CHECKS = 0 le rend moins critique.
---
TRUNCATE TABLE reservation;
TRUNCATE TABLE trajet;
TRUNCATE TABLE utilisateur;
TRUNCATE TABLE chauffeur;
TRUNCATE TABLE bus;

---
-- 3. Réinitialiser explicitement les séquences AUTO_INCREMENT.
--    Bien que TRUNCATE TABLE puisse réinitialiser AUTO_INCREMENT, cette étape explicite
--    garantit que les IDs repartent de 1, ce qui est crucial pour la cohérence avec
--    les IDs fixes utilisés dans les insertions ci-dessous.
---
ALTER TABLE utilisateur AUTO_INCREMENT = 1;
ALTER TABLE bus AUTO_INCREMENT = 1;
ALTER TABLE chauffeur AUTO_INCREMENT = 1;
ALTER TABLE trajet AUTO_INCREMENT = 1;
ALTER TABLE reservation AUTO_INCREMENT = 1;

---
-- 4. Insertion des données de test dans l'ordre des dépendances.
--    Les tables parentes (celles qui ne dépendent de personne) sont insérées en premier,
--    suivies par les tables enfants.
---

-- === UTILISATEURS TEST === (Aucune dépendance externe)
INSERT INTO utilisateur (nom, prenom, email, telephone, type_client, solde_compte, nombre_voyages) VALUES
('TEST-USER1', 'Client', 'test.user1@email.cm', '+237600000001', 'VIP', 100000.00, 5),
('TEST-USER2', 'Client', 'test.user2@email.cm', '+237600000002', 'REGULIER', 50000.00, 2);

-- === BUS === (Aucune dépendance externe)
INSERT INTO bus (immatriculation, type, capacite, statut, agence_actuelle, passagers_actuels) VALUES
('TEST-001-CM', 'VIP', 20, 'DISPONIBLE', 'Yaoundé Centre', 0),
('TEST-002-CM', 'CLASSIQUE', 50, 'DISPONIBLE', 'Douala Port', 0);

-- === CHAUFFEURS === (Aucune dépendance externe)
INSERT INTO chauffeur (nom, prenom, numero_permis, statut, agence_actuelle, heures_travaillees) VALUES
('TEST-CH1', 'Jean', 'TEST-PC001', 'DISPONIBLE', 'Yaoundé Centre', 0),
('TEST-CH2', 'Marie', 'TEST-PC002', 'DISPONIBLE', 'Douala Port', 0);

-- === TRAJETS TEST === (Dépend de BUS et CHAUFFEUR)
-- Les bus_id et chauffeur_id (1 et 2) sont maintenant garantis d'exister.
-- Les dates de départ ont été ajustées avec des heures différentes pour maximiser l'unicité
-- et minimiser les risques de violation de contraintes UNIQUE sur des colonnes temporelles ou combinées.
INSERT INTO trajet (agence_origine, agence_destination, date_depart, date_arrivee, bus_id, chauffeur_id, statut, prix, places_reservees) VALUES
('Yaoundé Centre', 'Douala Port', NOW() + INTERVAL 1 DAY + INTERVAL 9 HOUR, NOW() + INTERVAL 1 DAY + INTERVAL 14 HOUR, 1, 1, 'PLANIFIE', 15000.00, 0),
('Douala Port', 'Yaoundé Centre', NOW() + INTERVAL 2 DAY + INTERVAL 10 HOUR, NOW() + INTERVAL 2 DAY + INTERVAL 17 HOUR, 2, 2, 'PLANIFIE', 15000.00, 0),
('Yaoundé Centre', 'Bafoussam', NOW() + INTERVAL 3 DAY + INTERVAL 8 HOUR, NOW() + INTERVAL 3 DAY + INTERVAL 11 HOUR, 1, 2, 'PLANIFIE', 10000.00, 0); -- Ajout d'un troisième trajet pour plus de diversité et tester une nouvelle combinaison

-- === RESERVATIONS TEST === (Dépend de UTILISATEUR et TRAJET)
-- Les utilisateur_id et trajet_id (1, 2, et maintenant 3) sont maintenant garantis d'exister.
INSERT INTO reservation (utilisateur_id, trajet_id, nombre_places, montant_total, statut, date_reservation, date_paiement, numero_reservation) VALUES
(1, 1, 2, 30000.00, 'CONFIRMEE', NOW(), NOW(), 'TEST-RES-001'),
(2, 2, 1, 15000.00, 'EN_ATTENTE', NOW(), NULL, 'TEST-RES-002'),
(1, 3, 1, 10000.00, 'CONFIRMEE', NOW(), NOW(), 'TEST-RES-003'); -- Réservation pour le nouveau trajet

---
-- 5. Réactiver les contrôles de clés étrangères.
--    C'est crucial pour maintenir l'intégrité référentielle après l'initialisation des données.
---
SET FOREIGN_KEY_CHECKS = 1;

---
-- 6. Création de la vue (si nécessaire pour les tests et non gérée par JPA).
--    DOIT ÊTRE À LA FIN, APRÈS TOUTES LES INSERTIONS de données.
---
DROP VIEW IF EXISTS v_statistiques_agence;
CREATE VIEW v_statistiques_agence AS
SELECT
    'Bus disponibles' AS metric,
    COUNT(*) AS value
FROM bus WHERE statut = 'DISPONIBLE'
UNION ALL
SELECT
    'Chauffeurs disponibles',
    COUNT(*)
FROM chauffeur WHERE statut = 'DISPONIBLE'
UNION ALL
SELECT
    'Trajets planifiés',
    COUNT(*)
FROM trajet WHERE statut = 'PLANIFIE'
UNION ALL
SELECT
    'Réservations confirmées',
    COUNT(*)
FROM reservation WHERE statut = 'CONFIRMEE';