package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Controller Produit - Version corrigée pour SonarQube
 * =====================================================
 * Toutes les mauvaises pratiques ont été éliminées.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    // Constante pour éviter les "Magic Strings"
    private static final String PREMIUM_CATEGORY = "Premium";
    private static final double PREMIUM_THRESHOLD = 100.0;
    private static final double EXPENSIVE_THRESHOLD = 50.0;
    private static final int HIGH_STOCK_THRESHOLD = 1000;
    private static final double MARGIN_RATE = 0.3;

    private final ProductRepository repo;

    // Injection par constructeur (meilleure pratique Spring)
    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * GET /api/products - Récupérer tous les produits
     */
    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Récupération de tous les produits");

        List<Product> products = repo.findAll();
        log.info("Nombre de produits: {}", products.size());

        return products;
    }

    /**
     * GET /api/products/{id} - Récupérer un produit par ID
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        log.info("Recherche produit ID = {}", id);

        Product product = repo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé avec ID: " + id));

        log.info("Produit trouvé: {}", product.getName());
        return product;
    }

    /**
     * GET /api/products/category/{category} - Produits par catégorie
     */
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        log.info("Recherche catégorie = {}", category);

        // Comparaison correcte avec .equals()
        if ("Electronics".equals(category)) {
            log.info("Catégorie électronique détectée");
        }

        if (PREMIUM_CATEGORY.equals(category)) {
            log.info("Catégorie premium!");
        }

        return repo.findByCategory(category);
    }

    /**
     * POST /api/products - Créer un produit
     */
    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        log.info("Création produit: {}", product.getName());

        try {
            // Validation
            if (product.getName() == null || product.getName().isEmpty()) {
                log.error("Nom vide");
                return ResponseEntity.badRequest().body("Le nom est requis");
            }

            // Utilisation de la constante PREMIUM_THRESHOLD
            if (product.getPrice() != null && product.getPrice() > PREMIUM_THRESHOLD) {
                log.info("Produit premium détecté (prix > {})", PREMIUM_THRESHOLD);
                product.setCategory(PREMIUM_CATEGORY);
            }

            if (product.getStock() != null && product.getStock() > HIGH_STOCK_THRESHOLD) {
                log.info("Stock élevé détecté (> {})", HIGH_STOCK_THRESHOLD);
            }

            if (product.getPrice() != null) {
                Double margin = product.getPrice() * MARGIN_RATE;
                log.info("Marge calculée: {}", margin);
            }

            Product saved = repo.save(product);
            log.info("Produit sauvegardé ID = {}", saved.getId());

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Erreur lors de la création du produit: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création du produit");
        }
    }

    /**
     * PUT /api/products/{id} - Mettre à jour un produit
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        log.info("Mise à jour produit ID = {}", id);

        try {
            Product product = repo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Produit non trouvé avec ID: " + id));

            product.setName(productDetails.getName());
            product.setCategory(productDetails.getCategory());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());
            product.setDescription(productDetails.getDescription());

            // Validation avec .isEmpty() au lieu de == ""
            if (product.getName() == null || product.getName().isEmpty()) {
                log.error("Nom invalide");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom est invalide");
            }

            // Utilisation de la constante
            if (product.getPrice() > PREMIUM_THRESHOLD) {
                product.setCategory(PREMIUM_CATEGORY);
            }

            Product updated = repo.save(product);
            log.info("Produit mis à jour");

            return updated;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du produit: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la mise à jour du produit");
        }
    }

    /**
     * DELETE /api/products/{id} - Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        log.info("Suppression produit ID = {}", id);

        try {
            if (!repo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé avec ID: " + id);
            }
            repo.deleteById(id);
            log.info("Produit supprimé");
            return ResponseEntity.ok("Supprimé");

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du produit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur");
        }
    }

    /**
     * GET /api/products/search - Rechercher des produits
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        log.info("Recherche produits avec nom = {}", name);

        try {
            List<Product> results = repo.findByNameContaining(name);
            log.info("{} résultats trouvés", results.size());
            return results;

        } catch (Exception e) {
            log.error("Erreur lors de la recherche: {}", e.getMessage(), e);
            // Retourne une liste vide au lieu de null
            return Collections.emptyList();
        }
    }

    /**
     * GET /api/products/expensive - Produits chers
     */
    @GetMapping("/expensive")
    public List<Product> getExpensiveProducts() {
        log.info("Recherche produits chers");
        return repo.findByPriceGreaterThan(EXPENSIVE_THRESHOLD);
    }

    /**
     * GET /api/products/instock - Produits en stock
     */
    @GetMapping("/instock")
    public List<Product> getInStockProducts() {
        log.info("Recherche produits en stock");

        try {
            return repo.findByStockGreaterThan(0);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des produits en stock: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
