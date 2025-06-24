-- src/main/resources/schema.sql
-- Script de création de schéma pour la base de données de développement 'policydb'.
-- Gère la création des tables, clés étrangères, index et vues.

-- Désactiver temporairement les contrôles de clés étrangères pour permettre les DROP TABLE sans violation
SET FOREIGN_KEY_CHECKS = 0;

-- Suppression des tables dans l'ordre inverse des dépendances pour garantir un nettoyage complet
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS trajet;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS chauffeur;
DROP TABLE IF EXISTS bus;

-- Suppression des vues (doit être fait avant de recréer les tables si les vues dépendent des tables)
DROP VIEW IF EXISTS v_statistiques_agence;

-- Réactiver les contrôles de clés étrangères (sera réactivé après la création de toutes les tables)
SET FOREIGN_KEY_CHECKS = 1;

-- Table des utilisateurs/clients
CREATE TABLE IF NOT EXISTS utilisateur (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    type_client ENUM('VIP', 'REGULIER', 'OCCASIONNEL') NOT NULL DEFAULT 'OCCASIONNEL',
    solde_compte DECIMAL(10,2) DEFAULT 0.00 CHECK (solde_compte >= 0),
    nombre_voyages INT DEFAULT 0 CHECK (nombre_voyages >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des chauffeurs
CREATE TABLE IF NOT EXISTS chauffeur (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    numero_permis VARCHAR(50) UNIQUE NOT NULL,
    statut ENUM('DISPONIBLE', 'EN_SERVICE', 'REPOS', 'CONGE', 'SUSPENDU') NOT NULL DEFAULT 'DISPONIBLE',
    agence_actuelle VARCHAR(100) NOT NULL,
    heures_travaillees INT DEFAULT 0 CHECK (heures_travaillees >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des bus
CREATE TABLE IF NOT EXISTS bus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    immatriculation VARCHAR(50) UNIQUE NOT NULL,
    type ENUM('VIP', 'CLASSIQUE') NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    statut ENUM('DISPONIBLE', 'EN_ROUTE', 'MAINTENANCE', 'HORS_SERVICE') NOT NULL DEFAULT 'DISPONIBLE',
    agence_actuelle VARCHAR(100) NOT NULL,
    passagers_actuels INT DEFAULT 0 CHECK (passagers_actuels >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des trajets
CREATE TABLE IF NOT EXISTS trajet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    agence_origine VARCHAR(100) NOT NULL,
    agence_destination VARCHAR(100) NOT NULL,
    date_depart DATETIME NOT NULL,
    date_arrivee DATETIME NOT NULL,
    bus_id BIGINT NOT NULL,
    chauffeur_id BIGINT NOT NULL,
    statut ENUM('PLANIFIE', 'EN_COURS', 'TERMINE', 'ANNULE') NOT NULL DEFAULT 'PLANIFIE',
    prix DECIMAL(10,2) NOT NULL CHECK (prix > 0),
    places_reservees INT DEFAULT 0 CHECK (places_reservees >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (date_arrivee > date_depart),
    FOREIGN KEY (bus_id) REFERENCES bus(id) ON DELETE RESTRICT,
    FOREIGN KEY (chauffeur_id) REFERENCES chauffeur(id) ON DELETE RESTRICT
);

-- Table des réservations
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id BIGINT NOT NULL,
    trajet_id BIGINT NOT NULL,
    nombre_places INT NOT NULL CHECK (nombre_places > 0),
    montant_total DECIMAL(10,2) NOT NULL CHECK (montant_total > 0),
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'PAYEE', 'ANNULEE', 'REMBOURSEE') NOT NULL DEFAULT 'EN_ATTENTE',
    date_reservation DATETIME NOT NULL,
    date_paiement DATETIME NULL,
    numero_reservation VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    FOREIGN KEY (trajet_id) REFERENCES trajet(id) ON DELETE RESTRICT
);

-- Index pour optimiser les performances
CREATE INDEX IF NOT EXISTS idx_bus_statut ON bus(statut);
CREATE INDEX IF NOT EXISTS idx_chauffeur_statut ON chauffeur(statut);
CREATE INDEX IF NOT EXISTS idx_trajet_statut ON trajet(statut);
CREATE INDEX IF NOT EXISTS idx_trajet_dates ON trajet(date_depart, date_arrivee);
CREATE INDEX IF NOT EXISTS idx_reservation_statut ON reservation(statut);
CREATE INDEX IF NOT EXISTS idx_reservation_date ON reservation(date_reservation);
CREATE INDEX IF NOT EXISTS idx_utilisateur_type ON utilisateur(type_client);

-- Vue pour les statistiques rapides
-- La vue est ici car elle fait partie du SCHEMA et n'est pas une simple donnée
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