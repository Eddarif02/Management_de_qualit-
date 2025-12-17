package com.example.demo.controller;


import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Commandes - ARCHITECTURE NAIVE
 * Contient toute la logique metier (calcul prix, verification stock)
 * USAGE EDUCATIF UNIQUEMENT
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // MAUVAISE PRATIQUE: Appel direct aux Repositories
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // MAUVAISE PRATIQUE: Constante magique
    private static final Double TAX_RATE = 0.20;
    private static final Double SHIPPING_COST = 5.99;

    /**
     * GET /api/orders - Recuperer toutes les commandes
     * MAUVAISE PRATIQUE: Expose les donnees clients sensibles
     */
    @GetMapping
    public List<Order> getAllOrders() {
        System.out.println("GET /api/orders - Recuperation de toutes les commandes");

        try {
            List<Order> orders = orderRepository.findAll();
            System.out.println("Nombre de commandes: " + orders.size());
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * GET /api/orders/{id} - Recuperer une commande par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        System.out.println("GET /api/orders/" + id);

        try {
            Order order = orderRepository.findById(id).orElse(null);

            if (order == null) {
                System.out.println("Commande non trouvee: " + id);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }

    /**
     * POST /api/orders - Passer une commande
     * MAUVAISE PRATIQUE: Toute la logique metier dans le Controller
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println("POST /api/orders - Nouvelle commande");
        System.out.println("Product ID: " + orderRequest.productId);
        System.out.println("Quantity: " + orderRequest.quantity);

        try {
            // MAUVAISE PRATIQUE: Validation manuelle imbriqu'ee
            if (orderRequest.productId == null) {
                System.out.println("Erreur: productId manquant");
                return ResponseEntity.badRequest().body("Product ID requis");
            }

            if (orderRequest.quantity == null || orderRequest.quantity <= 0) {
                System.out.println("Erreur: quantite invalide");
                return ResponseEntity.badRequest().body("Quantite doit etre positive");
            }

            // Recuperer le produit
            Product product = productRepository.findById(orderRequest.productId).orElse(null);

            // MAUVAISE PRATIQUE: Verification nulle apres utilisation potentielle
            if (product == null) {
                System.out.println("Erreur: produit non trouve");
                return ResponseEntity.badRequest().body("Produit non trouve");
            }

            System.out.println("Produit trouve: " + product.getName());
            System.out.println("Stock actuel: " + product.getStock());

            // LOGIQUE METIER DANS LE CONTROLLER: Verification du stock
            if (product.getStock() < orderRequest.quantity) {
                System.out.println("Erreur: stock insuffisant");
                System.out.println("Stock disponible: " + product.getStock());
                System.out.println("Quantite demandee: " + orderRequest.quantity);
                return ResponseEntity.badRequest().body("Stock insuffisant. Disponible: " + product.getStock());
            }

            // LOGIQUE METIER DANS LE CONTROLLER: Calcul du prix total
            Double subtotal = product.getPrice() * orderRequest.quantity;
            Double tax = subtotal * TAX_RATE;
            Double totalPrice = subtotal + tax + SHIPPING_COST;

            System.out.println("Calcul du prix:");
            System.out.println("  Sous-total: " + subtotal);
            System.out.println("  TVA (20%): " + tax);
            System.out.println("  Frais de port: " + SHIPPING_COST);
            System.out.println("  TOTAL: " + totalPrice);

            // LOGIQUE METIER: Reduction du stock
            int newStock = product.getStock() - orderRequest.quantity;
            product.setStock(newStock);
            productRepository.save(product);
            System.out.println("Nouveau stock: " + newStock);

            // Creation de la commande
            Order order = new Order();
            order.setProductId(product.getId());
            order.setProductName(product.getName());
            order.setQuantity(orderRequest.quantity);
            order.setUnitPrice(product.getPrice());
            order.setTotalPrice(totalPrice);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("CONFIRMED");
            order.setCustomerEmail(orderRequest.customerEmail);
            order.setCustomerPhone(orderRequest.customerPhone);

            Order savedOrder = orderRepository.save(order);
            System.out.println("Commande creee avec ID: " + savedOrder.getId());

            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {
            // MAUVAISE PRATIQUE: catch generique avec printStackTrace
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la creation de la commande");
        }
    }

    /**
     * PUT /api/orders/{id}/cancel - Annuler une commande
     * MAUVAISE PRATIQUE: Logique de remboursement stock dans le Controller
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        System.out.println("PUT /api/orders/" + id + "/cancel");

        try {
            Order order = orderRepository.findById(id).orElse(null);

            if (order == null) {
                System.out.println("Commande non trouvee: " + id);
                return ResponseEntity.notFound().build();
            }

            // MAUVAISE PRATIQUE: Comparaison String avec ==
            if (order.getStatus() == "CANCELLED") {
                return ResponseEntity.badRequest().body("Commande deja annulee");
            }

            // Restaurer le stock
            Product product = productRepository.findById(order.getProductId()).orElse(null);
            if (product != null) {
                int restoredStock = product.getStock() + order.getQuantity();
                product.setStock(restoredStock);
                productRepository.save(product);
                System.out.println("Stock restaure: " + restoredStock);
            }

            order.setStatus("CANCELLED");
            orderRepository.save(order);
            System.out.println("Commande annulee: " + id);

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'annulation");
        }
    }

    /**
     * GET /api/orders/by-product/{productId} - Commandes par produit
     */
    @GetMapping("/by-product/{productId}")
    public List<Order> getOrdersByProduct(@PathVariable Long productId) {
        System.out.println("GET /api/orders/by-product/" + productId);

        try {
            return orderRepository.findByProductId(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * GET /api/orders/by-status/{status} - Commandes par statut
     */
    @GetMapping("/by-status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        System.out.println("GET /api/orders/by-status/" + status);

        try {
            return orderRepository.findByStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * DELETE /api/orders/{id} - Supprimer une commande
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        System.out.println("DELETE /api/orders/" + id);

        try {
            orderRepository.deleteById(id);
            System.out.println("Commande supprimee: " + id);
            return ResponseEntity.ok("Commande supprimee");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la suppression");
        }
    }

    // Methode inutilisee
    private Double calculateDiscount(Double price, Integer quantity) {
        System.out.println("Calcul remise...");
        if (quantity > 10) {
            return price * 0.10;
        } else if (quantity > 5) {
            return price * 0.05;
        }
        return 0.0;
    }
}
