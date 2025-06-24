package com.policyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale PolicyAPI
 * Modélisation Mathématique des Politiques pour Agence de Transport
 * Yaoundé - Cameroun
 *
 * @author NJEMPOU YAMPEN Rachida Rouchda, N'UNGANG MBOUM Freddy Lionnel, HEUDEP DJANDJA Brian Brusly
 * @version 1.0.0
 */
@SpringBootApplication
public class PolicyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyApiApplication.class, args);
        System.out.println("=================================");
        System.out.println("PolicyAPI - Agence Transport");
        System.out.println("Gestion et externalisation des Politiques");
        System.out.println("Application démarrée avec succès!");
        System.out.println("API disponible sur: http://localhost:8080");
        System.out.println("=================================");
    }
}