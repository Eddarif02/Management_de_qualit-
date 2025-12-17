package com.example.demo.controller;

// MAUVAISE PRATIQUE: Imports inutilisés
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.io.File;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller Produit - CONTIENT INTENTIONNELLEMENT DES MAUVAISES PRATIQUES
 * ========================================================================
 * Ce fichier est conçu pour un TP SonarQube.
 * NE PAS UTILISER EN PRODUCTION !
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // MAUVAISE PRATIQUE #1: Field Injection au lieu de Constructor Injection
    @Autowired
    private ProductRepository repo;

    // MAUVAISE PRATIQUE #2: Variable inutilisée (Code mort)
    private String unusedVariable = "Je ne sers a rien";
    private int counter = 0;

    // MAUVAISE PRATIQUE #3: Magic number sans constante
    // On devrait avoir: private static final double PREMIUM_THRESHOLD = 100.0;

    /**
     * GET /api/products - Récupérer tous les produits
     * MAUVAISES PRATIQUES:
     * - System.out au lieu de Logger
     * - Retourne l'Entity directement (pas de DTO)
     */
    @GetMapping
    public List<Product> getAllProducts() {
        System.out.println("DEBUG: Recuperation de tous les produits");

        // MAUVAISE PRATIQUE: Variable non explicite
        List<Product> l = repo.findAll();
        System.out.println("DEBUG: Nombre de produits: " + l.size());

        return l;
    }

    /**
     * GET /api/products/{id} - Récupérer un produit par ID
     * MAUVAISES PRATIQUES:
     * - Retourne null si non trouvé (devrait être 404)
     * - NullPointerException possible
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        System.out.println("DEBUG: Recherche produit ID = " + id);

        // MAUVAISE PRATIQUE: Retourne null au lieu de gérer l'erreur proprement
        Product p = repo.findById(id).orElse(null);

        // BUG POTENTIEL: NullPointerException si p est null
        System.out.println("DEBUG: Produit trouve: " + p.getName());

        return p;
    }

    /**
     * GET /api/products/category/{category} - Produits par catégorie
     * MAUVAISE PRATIQUE MAJEURE: Comparaison String avec == au lieu de .equals()
     */
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        System.out.println("DEBUG: Recherche categorie = " + category);

        // BUG: Comparaison de String avec == (toujours false sauf cas rares)
        if (category == "Electronics") {
            System.out.println("DEBUG: Categorie electronique detectee");
        }

        // MAUVAISE PRATIQUE: Autre comparaison incorrecte
        if (category == "Premium") {
            System.out.println("DEBUG: Categorie premium!");
        }

        List<Product> products = repo.findByCategory(category);
        return products;
    }

    /**
     * POST /api/products - Créer un produit
     * MAUVAISES PRATIQUES:
     * - Logique métier dans le Controller
     * - Validation manuelle sans Bean Validation
     * - Magic numbers
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product p) {
        System.out.println("DEBUG: Creation produit: " + p.getName());

        try {
            // MAUVAISE PRATIQUE: Validation manuelle dans le Controller
            // Devrait être dans un Service avec @Valid
            if (p.getName() == null || p.getName() == "") {
                System.out.println("ERREUR: Nom vide");
                return ResponseEntity.badRequest().body("Le nom est requis");
            }

            // MAUVAISE PRATIQUE: Magic number (100) sans constante
            if (p.getPrice() != null && p.getPrice() > 100) {
                System.out.println("DEBUG: Produit premium detecte (prix > 100)");
                p.setCategory("Premium");
            }

            // MAUVAISE PRATIQUE: Magic number (1000)
            if (p.getStock() != null && p.getStock() > 1000) {
                System.out.println("DEBUG: Stock eleve detecte (> 1000)");
            }

            // MAUVAISE PRATIQUE: Calcul métier dans le Controller
            if (p.getPrice() != null) {
                // Calcul de la marge (devrait être dans un Service)
                Double margin = p.getPrice() * 0.3; // Magic number 0.3
                System.out.println("DEBUG: Marge calculee: " + margin);
            }

            Product saved = repo.save(p);
            System.out.println("DEBUG: Produit sauvegarde ID = " + saved.getId());

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            // MAUVAISE PRATIQUE: Catch vide qui avale l'exception
            e.printStackTrace();
            return null; // MAUVAISE PRATIQUE: Retourne null
        }
    }

    /**
     * PUT /api/products/{id} - Mettre à jour un produit
     * MAUVAISES PRATIQUES:
     * - Duplication de code (validation)
     * - Gestion d'erreur silencieuse
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        System.out.println("DEBUG: Mise a jour produit ID = " + id);

        try {
            // MAUVAISE PRATIQUE: Pas de vérification si le produit existe
            Product p = repo.findById(id).orElse(null);

            // BUG: NullPointerException si p est null
            p.setName(productDetails.getName());
            p.setCategory(productDetails.getCategory());
            p.setPrice(productDetails.getPrice());
            p.setStock(productDetails.getStock());
            p.setDescription(productDetails.getDescription());

            // MAUVAISE PRATIQUE: Duplication du code de validation
            if (p.getName() == null || p.getName() == "") {
                System.out.println("ERREUR: Nom invalide");
                return null;
            }

            // MAUVAISE PRATIQUE: Magic number dupliqué
            if (p.getPrice() > 100) {
                p.setCategory("Premium");
            }

            Product updated = repo.save(p);
            System.out.println("DEBUG: Produit mis a jour");

            return updated;

        } catch (Exception e) {
            // MAUVAISE PRATIQUE: Catch silencieux
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DELETE /api/products/{id} - Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        System.out.println("DEBUG: Suppression produit ID = " + id);

        try {
            // MAUVAISE PRATIQUE: Pas de vérification d'existence
            repo.deleteById(id);
            System.out.println("DEBUG: Produit supprime");
            return ResponseEntity.ok("Supprime");

        } catch (Exception e) {
            // MAUVAISE PRATIQUE: printStackTrace
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur");
        }
    }

    /**
     * GET /api/products/search - Rechercher des produits
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        System.out.println("DEBUG: Recherche produits avec nom = " + name);

        try {
            List<Product> results = repo.findByNameContaining(name);
            System.out.println("DEBUG: " + results.size() + " resultats");
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            // MAUVAISE PRATIQUE: Retourne null en cas d'erreur
            return null;
        }
    }

    /**
     * GET /api/products/expensive - Produits chers
     * MAUVAISE PRATIQUE: Magic number
     */
    @GetMapping("/expensive")
    public List<Product> getExpensiveProducts() {
        System.out.println("DEBUG: Recherche produits chers");

        // MAUVAISE PRATIQUE: Magic number 50.0 en dur
        return repo.findByPriceGreaterThan(50.0);
    }

    /**
     * GET /api/products/instock - Produits en stock
     */
    @GetMapping("/instock")
    public List<Product> getInStockProducts() {
        System.out.println("DEBUG: Recherche produits en stock");

        try {
            return repo.findByStockGreaterThan(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================================================
    // MAUVAISE PRATIQUE: Code mort / Méthodes jamais utilisées
    // =========================================================

    private void unusedMethod() {
        System.out.println("Cette methode n'est jamais appelee");
        int x = 5;
        int y = 10;
        int z = x + y;
    }

    private String anotherUnusedMethod(String param) {
        return "unused: " + param;
    }

    // MAUVAISE PRATIQUE: Code commenté laissé dans le fichier
    /*
     * @GetMapping("/old-endpoint")
     * public List<Product> oldMethod() {
     * System.out.println("Ancienne methode");
     * return repo.findAll();
     * }
     * 
     * private void deprecatedLogic() {
     * // TODO: A supprimer
     * for (int i = 0; i < 100; i++) {
     * System.out.println(i);
     * }
     * }
     */
}
