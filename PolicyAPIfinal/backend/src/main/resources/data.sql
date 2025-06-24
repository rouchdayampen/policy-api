-- src/main/resources/data.sql
-- Ce script est exécuté APRES schema.sql pour insérer les données initiales
-- pour l'environnement de développement.

---
-- 1. Désactiver temporairement les contrôles de clés étrangères.
---
SET FOREIGN_KEY_CHECKS = 0;

---
-- 2. Vider toutes les tables de données en utilisant TRUNCATE TABLE.
---
TRUNCATE TABLE reservation;
TRUNCATE TABLE trajet;
TRUNCATE TABLE utilisateur;
TRUNCATE TABLE chauffeur;
TRUNCATE TABLE bus;

---
-- 3. Réinitialiser explicitement les séquences AUTO_INCREMENT.
---
ALTER TABLE utilisateur AUTO_INCREMENT = 1;
ALTER TABLE bus AUTO_INCREMENT = 1;
ALTER TABLE chauffeur AUTO_INCREMENT = 1;
ALTER TABLE trajet AUTO_INCREMENT = 1;
ALTER TABLE reservation AUTO_INCREMENT = 1;

---
-- 4. Insertion des données de base.
-- Les IDs des clés étrangères (bus_id, chauffeur_id, utilisateur_id, trajet_id)
-- doivent correspondre aux IDs générés par les insertions précédentes.
---

-- === INSERTION DES BUS ===
INSERT INTO bus (immatriculation, type, capacite, statut, agence_actuelle, passagers_actuels) VALUES
-- Bus VIP (Confort Premium)
('LT-001-CM', 'VIP', 20, 'DISPONIBLE', 'Yaoundé Centre', 0),
('LT-002-CM', 'VIP', 25, 'DISPONIBLE', 'Douala Port', 0),
('LT-003-CM', 'VIP', 22, 'EN_ROUTE', 'Bafoussam', 15),
('LT-004-CM', 'VIP', 24, 'MAINTENANCE', 'Yaoundé Centre', 0),

-- Bus Classiques (Standard)
('CE-101-CM', 'CLASSIQUE', 70, 'DISPONIBLE', 'Yaoundé Centre', 0),
('CE-102-CM', 'CLASSIQUE', 65, 'DISPONIBLE', 'Douala Port', 0),
('CE-103-CM', 'CLASSIQUE', 75, 'EN_ROUTE', 'Bamenda', 45),
('CE-104-CM', 'CLASSIQUE', 70, 'HORS_SERVICE', 'Garoua', 0);

-- === INSERTION DES CHAUFFEURS ===
INSERT INTO chauffeur (nom, prenom, numero_permis, statut, agence_actuelle, heures_travaillees) VALUES
-- Chauffeurs expérimentés
('MBARGA', 'Jean-Claude', 'PC001YDE', 'DISPONIBLE', 'Yaoundé Centre', 2),
('FOUDA', 'Marie-Claire', 'PC002DLA', 'DISPONIBLE', 'Douala Port', 0),
('TAGNE', 'Paul-Eric', 'PC003BFM', 'EN_SERVICE', 'Bafoussam', 6),
('NKOMO', 'Sylvie', 'PC004YDE', 'REPOS', 'Yaoundé Centre', 8),
('BIYA', 'François', 'PC005BMD', 'DISPONIBLE', 'Bamenda', 3),
('ONANA', 'Georgette', 'PC006GAR', 'CONGE', 'Garoua', 0);

-- === INSERTION DES UTILISATEURS ===
INSERT INTO utilisateur (nom, prenom, email, telephone, type_client, solde_compte, nombre_voyages) VALUES
-- Clients VIP (Privilégiés)
('BELLO', 'Amadou', 'amadou.bello@email.cm', '+237678901234', 'VIP', 250000.00, 25),
('KAMGA', 'Solange', 'solange.kamga@email.cm', '+237690123456', 'VIP', 180000.00, 18),

-- Clients Réguliers (Fidèles)
('NGUEMA', 'Pierre', 'pierre.nguema@email.cm', '+237691234567', 'REGULIER', 75000.00, 12),
('ESSOMBA', 'Françoise', 'francoise.essomba@email.cm', '+237682345678', 'REGULIER', 45000.00, 8),

-- Client Occasionnel
('ATEBA', 'Martin', 'martin.ateba@email.cm', '+237693456789', 'OCCASIONNEL', 25000.00, 2);

-- === INSERTION DES TRAJETS ===
INSERT INTO trajet (agence_origine, agence_destination, date_depart, date_arrivee, bus_id, chauffeur_id, statut, prix, places_reservees) VALUES
-- Trajets VIP planifiés (IDs bus 1,2,3,4; chauffeurs 1,2,3,4,5,6)
('Yaoundé Centre', 'Douala Port', '2024-12-20 08:00:00', '2024-12-20 12:00:00', 1, 1, 'PLANIFIE', 15000.00, 8), -- Bus 1, Chauffeur 1
('Douala Port', 'Bafoussam', '2024-12-20 14:00:00', '2024-12-20 17:30:00', 2, 2, 'PLANIFIE', 18000.00, 5), -- Bus 2, Chauffeur 2

-- Trajets Classiques planifiés (IDs bus 5,6,7,8; chauffeurs 1,2,3,4,5,6)
('Yaoundé Centre', 'Bamenda', '2024-12-20 09:00:00', '2024-12-20 15:00:00', 5, 5, 'PLANIFIE', 8000.00, 42), -- Bus 5, Chauffeur 5
('Douala Port', 'Garoua', '2024-12-21 06:00:00', '2024-12-21 18:00:00', 6, 2, 'PLANIFIE', 25000.00, 28), -- Bus 6, Chauffeur 2

-- Trajets en cours
('Bafoussam', 'Yaoundé Centre', '2024-12-19 15:00:00', '2024-12-19 19:00:00', 3, 3, 'EN_COURS', 18000.00, 15), -- Bus 3, Chauffeur 3

-- Trajet terminé
('Bamenda', 'Douala Port', '2024-12-18 07:00:00', '2024-12-18 13:00:00', 7, 5, 'TERMINE', 12000.00, 45); -- Bus 7, Chauffeur 5

-- === INSERTION DES RESERVATIONS ===
INSERT INTO reservation (utilisateur_id, trajet_id, nombre_places, montant_total, statut, date_reservation, date_paiement, numero_reservation) VALUES
-- Réservations confirmées (IDs utilisateur 1,2,3,4,5; trajets 1,2,3,4,5,6)
(1, 1, 2, 30000.00, 'CONFIRMEE', '2024-12-18 10:30:00', '2024-12-18 10:35:00', 'RES1734518400001'),
(2, 1, 3, 45000.00, 'CONFIRMEE', '2024-12-18 11:15:00', '2024-12-18 11:20:00', 'RES1734521700002'),
(3, 2, 1, 18000.00, 'CONFIRMEE', '2024-12-18 14:20:00', '2024-12-18 14:25:00', 'RES1734532800003'),
(1, 3, 5, 40000.00, 'CONFIRMEE', '2024-12-18 16:45:00', '2024-12-18 16:50:00', 'RES1734541500004'),

-- Réservations en attente
(4, 2, 2, 36000.00, 'EN_ATTENTE', '2024-12-19 09:30:00', NULL, 'RES1734602200005'),
(5, 4, 1, 25000.00, 'EN_ATTENTE', '2024-12-19 11:00:00', NULL, 'RES1734607200006');

---
-- 5. Réactiver les contrôles de clés étrangères.
---
SET FOREIGN_KEY_CHECKS = 1;