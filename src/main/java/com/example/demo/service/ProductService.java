package com.example.demo.service;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;

import java.util.List;

/**
 * Interface du service Product.
 * Définit le contrat pour les opérations métier sur les produits.
 */
public interface ProductService {

    List<ProductResponseDTO> findAll();

    ProductResponseDTO findById(Long id);

    List<ProductResponseDTO> searchByName(String name);

    List<ProductResponseDTO> findAvailable();

    ProductResponseDTO create(ProductRequestDTO productRequest);

    ProductResponseDTO update(Long id, ProductRequestDTO productRequest);

    void delete(Long id);
}
