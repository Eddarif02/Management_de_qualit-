package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des produits.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Recherche les produits dont le nom contient la chaîne spécifiée.
     */
    List<Product> findByNameContaining(String name);

    /**
     * Recherche les produits dont le stock est supérieur à la valeur spécifiée.
     */
    List<Product> findByStockGreaterThan(Integer stock);
}
