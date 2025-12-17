package com.example.demo.service;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service Product.
 * Contient toute la logique métier liée aux produits.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final String PRODUCT_RESOURCE_NAME = "Produit";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Injection par constructeur (recommandé par SonarQube)
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        log.debug("Récupération de tous les produits");
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        log.debug("Récupération du produit avec l'ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_RESOURCE_NAME, id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchByName(String name) {
        log.debug("Recherche de produits avec le nom contenant: {}", name);
        return productRepository.findByNameContaining(name).stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAvailable() {
        log.debug("Récupération des produits disponibles en stock");
        return productRepository.findByStockGreaterThan(0).stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO productRequest) {
        log.info("Création d'un nouveau produit: {}", productRequest.name());
        Product product = productMapper.toEntity(productRequest);
        Product savedProduct = productRepository.save(product);
        log.info("Produit créé avec l'ID: {}", savedProduct.getId());
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequest) {
        log.info("Mise à jour du produit avec l'ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_RESOURCE_NAME, id));

        productMapper.updateEntityFromDTO(productRequest, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Produit mis à jour: {}", updatedProduct.getName());
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du produit avec l'ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(PRODUCT_RESOURCE_NAME, id);
        }
        productRepository.deleteById(id);
        log.info("Produit supprimé avec succès");
    }
}
