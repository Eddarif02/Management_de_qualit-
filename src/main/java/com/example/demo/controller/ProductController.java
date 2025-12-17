package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller Produit - ARCHITECTURE NAIVE
 * Pas de Service layer, logique metier dans le Controller
 * USAGE EDUCATIF UNIQUEMENT
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // MAUVAISE PRATIQUE: Appel direct au Repository sans Service
    @Autowired
    private ProductRepository productRepository;

    // Variable inutilisee
    private String unusedField = "not used";

    /**
     * GET /api/products - Recuperer tous les produits
     * MAUVAISE PRATIQUE: Retourne l'entite directement sans DTO
     */
    @GetMapping
    public List<Product> getAllProducts() {
        System.out.println("GET /api/products - Recuperation de tous les produits");

        try {
            List<Product> products = productRepository.findAll();
            System.out.println("Nombre de produits trouves: " + products.size());
            return products;
        } catch (Exception e) {
            // MAUVAISE PRATIQUE: printStackTrace et retourne liste vide
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * GET /api/products/{id} - Recuperer un produit par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        System.out.println("GET /api/products/" + id);

        try {
            Product product = productRepository.findById(id).orElse(null);

            // MAUVAISE PRATIQUE: pas de gestion propre du null
            if (product == null) {
                System.out.println("Produit non trouve: " + id);
                return ResponseEntity.notFound().build();
            }

            System.out.println("Produit trouve: " + product.getName());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }

    /**
     * POST /api/products - Creer un nouveau produit
     * MAUVAISE PRATIQUE: Validation dans le Controller
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        System.out.println("POST /api/products - Creation produit: " + product.getName());

        try {
            // MAUVAISE PRATIQUE: Validation manuelle dans le Controller
            if (product.getName() == null || product.getName() == "") {
                System.out.println("Erreur: nom produit vide");
                return ResponseEntity.badRequest().body("Le nom du produit est requis");
            }

            if (product.getPrice() == null || product.getPrice() < 0) {
                System.out.println("Erreur: prix invalide");
                return ResponseEntity.badRequest().body("Le prix doit etre positif");
            }

            if (product.getStock() == null || product.getStock() < 0) {
                System.out.println("Erreur: stock invalide");
                return ResponseEntity.badRequest().body("Le stock doit etre positif");
            }

            // Sauvegarde directe
            Product savedProduct = productRepository.save(product);
            System.out.println("Produit cree avec ID: " + savedProduct.getId());

            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la creation");
        }
    }

    /**
     * PUT /api/products/{id} - Mettre a jour un produit
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        System.out.println("PUT /api/products/" + id);

        try {
            Product product = productRepository.findById(id).orElse(null);

            // MAUVAISE PRATIQUE: NullPointerException possible
            if (product == null) {
                System.out.println("Produit non trouve pour mise a jour: " + id);
                return ResponseEntity.notFound().build();
            }

            // MAUVAISE PRATIQUE: Duplication de la validation
            if (productDetails.getName() == null || productDetails.getName() == "") {
                return ResponseEntity.badRequest().body("Le nom du produit est requis");
            }

            if (productDetails.getPrice() == null || productDetails.getPrice() < 0) {
                return ResponseEntity.badRequest().body("Le prix doit etre positif");
            }

            // Mise a jour des champs
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());

            Product updatedProduct = productRepository.save(product);
            System.out.println("Produit mis a jour: " + updatedProduct.getName());

            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la mise a jour");
        }
    }

    /**
     * DELETE /api/products/{id} - Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        System.out.println("DELETE /api/products/" + id);

        try {
            // MAUVAISE PRATIQUE: pas de verification si le produit existe
            productRepository.deleteById(id);
            System.out.println("Produit supprime: " + id);
            return ResponseEntity.ok("Produit supprime avec succes");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la suppression");
        }
    }

    /**
     * GET /api/products/search?name=xxx - Rechercher des produits
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        System.out.println("GET /api/products/search?name=" + name);

        try {
            List<Product> products = productRepository.findByNameContaining(name);
            System.out.println("Produits trouves: " + products.size());
            return products;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * GET /api/products/available - Produits en stock
     */
    @GetMapping("/available")
    public List<Product> getAvailableProducts() {
        System.out.println("GET /api/products/available");

        try {
            List<Product> products = productRepository.findByStockGreaterThan(0);
            System.out.println("Produits disponibles: " + products.size());
            return products;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Methode morte jamais utilisee
    private void unusedMethod() {
        System.out.println("Cette methode n'est jamais appelee");
    }
}
