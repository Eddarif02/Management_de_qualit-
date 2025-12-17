package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Entite Product - EXPOSEE DIRECTEMENT VIA L'API (mauvaise pratique)
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private Double price;

    private Integer stock;

    private String description;

    // MAUVAISE PRATIQUE: Champ interne qui sera expose via l'API
    private String internalSku = "SKU-INTERNAL-001";

    // MAUVAISE PRATIQUE: Donnee sensible exposee
    private Double costPrice;

    // Constructeurs
    public Product() {
    }

    public Product(String name, String category, Double price, Integer stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternalSku() {
        return internalSku;
    }

    public void setInternalSku(String internalSku) {
        this.internalSku = internalSku;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
}
