package com.example.demo.controller;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des produits.
 * Responsabilité unique : gérer les requêtes HTTP et déléguer au Service.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    // Injection par constructeur (recommandé par SonarQube)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products - Récupérer tous les produits
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.debug("GET /api/products");
        List<ProductResponseDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Récupérer un produit par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        log.debug("GET /api/products/{}", id);
        ProductResponseDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * GET /api/products/search?name=xxx - Rechercher des produits par nom
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String name) {
        log.debug("GET /api/products/search?name={}", name);
        List<ProductResponseDTO> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/available - Récupérer les produits en stock
     */
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponseDTO>> getAvailableProducts() {
        log.debug("GET /api/products/available");
        List<ProductResponseDTO> products = productService.findAvailable();
        return ResponseEntity.ok(products);
    }

    /**
     * POST /api/products - Créer un nouveau produit
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequest) {
        log.debug("POST /api/products");
        ProductResponseDTO createdProduct = productService.create(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * PUT /api/products/{id} - Mettre à jour un produit
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequest) {
        log.debug("PUT /api/products/{}", id);
        ProductResponseDTO updatedProduct = productService.update(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id} - Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("DELETE /api/products/{}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
