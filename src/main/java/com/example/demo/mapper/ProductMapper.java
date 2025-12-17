package com.example.demo.mapper;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre Product Entity et DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Convertit une entité Product vers un ProductResponseDTO.
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }

    /**
     * Convertit un ProductRequestDTO vers une entité Product.
     */
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Product(
                dto.name(),
                dto.price(),
                dto.stock());
    }

    /**
     * Met à jour une entité Product existante avec les données du DTO.
     */
    public void updateEntityFromDTO(ProductRequestDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
    }
}
