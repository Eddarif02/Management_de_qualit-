package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des commandes.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Recherche les commandes par ID de produit.
     */
    List<Order> findByProductId(Long productId);

    /**
     * Recherche les commandes par statut.
     */
    List<Order> findByStatus(String status);

    /**
     * Recherche les commandes par email client.
     */
    List<Order> findByCustomerEmail(String email);
}
