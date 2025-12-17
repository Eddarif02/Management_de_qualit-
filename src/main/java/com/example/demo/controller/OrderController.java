package com.example.demo.controller;

import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.OrderResponseDTO;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des commandes.
 * Responsabilité unique : gérer les requêtes HTTP et déléguer au Service.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    // Injection par constructeur (recommandé par SonarQube)
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/orders - Récupérer toutes les commandes
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.debug("GET /api/orders");
        List<OrderResponseDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id} - Récupérer une commande par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        log.debug("GET /api/orders/{}", id);
        OrderResponseDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders/by-product/{productId} - Commandes par produit
     */
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByProduct(@PathVariable Long productId) {
        log.debug("GET /api/orders/by-product/{}", productId);
        List<OrderResponseDTO> orders = orderService.findByProductId(productId);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/by-status/{status} - Commandes par statut
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable String status) {
        log.debug("GET /api/orders/by-status/{}", status);
        List<OrderResponseDTO> orders = orderService.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * POST /api/orders - Créer une nouvelle commande
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest) {
        log.debug("POST /api/orders");
        OrderResponseDTO createdOrder = orderService.create(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * PUT /api/orders/{id}/cancel - Annuler une commande
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        log.debug("PUT /api/orders/{}/cancel", id);
        OrderResponseDTO cancelledOrder = orderService.cancel(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    /**
     * DELETE /api/orders/{id} - Supprimer une commande
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("DELETE /api/orders/{}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
