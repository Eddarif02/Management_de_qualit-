package com.example.demo.service;

import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.OrderResponseDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Order.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.InvalidOperationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service Order.
 * Contient toute la logique métier liée aux commandes.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    // Constantes pour éviter les magic numbers/strings
    private static final double TAX_RATE = 0.20;
    private static final double SHIPPING_COST = 5.99;
    private static final String ORDER_RESOURCE_NAME = "Commande";
    private static final String PRODUCT_RESOURCE_NAME = "Produit";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    // Injection par constructeur (recommandé par SonarQube)
    public OrderServiceImpl(OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        log.debug("Récupération de toutes les commandes");
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        log.debug("Récupération de la commande avec l'ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_RESOURCE_NAME, id));
        return orderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByProductId(Long productId) {
        log.debug("Récupération des commandes pour le produit: {}", productId);
        return orderRepository.findByProductId(productId).stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByStatus(String status) {
        log.debug("Récupération des commandes avec le statut: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO orderRequest) {
        log.info("Création d'une nouvelle commande pour le produit: {}", orderRequest.productId());

        // Récupérer le produit
        Product product = productRepository.findById(orderRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_RESOURCE_NAME, orderRequest.productId()));

        // Vérifier le stock
        validateStock(product, orderRequest.quantity());

        // Calculer le prix total
        double totalPrice = calculateTotalPrice(product.getPrice(), orderRequest.quantity());

        // Réduire le stock
        reduceStock(product, orderRequest.quantity());

        // Créer la commande
        Order order = buildOrder(orderRequest, product, totalPrice);
        Order savedOrder = orderRepository.save(order);

        log.info("Commande créée avec l'ID: {} - Total: {}€", savedOrder.getId(), totalPrice);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO cancel(Long id) {
        log.info("Annulation de la commande: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_RESOURCE_NAME, id));

        // Vérifier si la commande n'est pas déjà annulée
        if (OrderStatus.CANCELLED.name().equals(order.getStatus())) {
            throw new InvalidOperationException("La commande est déjà annulée");
        }

        // Restaurer le stock
        restoreStock(order);

        // Mettre à jour le statut
        order.setStatus(OrderStatus.CANCELLED.name());
        Order cancelledOrder = orderRepository.save(order);

        log.info("Commande annulée avec succès: {}", id);
        return orderMapper.toResponseDTO(cancelledOrder);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la commande: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(ORDER_RESOURCE_NAME, id);
        }
        orderRepository.deleteById(id);
        log.info("Commande supprimée avec succès");
    }

    // --- Méthodes privées pour la logique métier ---

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException(
                    product.getName(),
                    requestedQuantity,
                    product.getStock());
        }
    }

    private double calculateTotalPrice(double unitPrice, int quantity) {
        double subtotal = unitPrice * quantity;
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax + SHIPPING_COST;
        log.debug("Calcul prix - Sous-total: {}€, TVA: {}€, Frais: {}€, Total: {}€",
                subtotal, tax, SHIPPING_COST, total);
        return total;
    }

    private void reduceStock(Product product, int quantity) {
        int newStock = product.getStock() - quantity;
        product.setStock(newStock);
        productRepository.save(product);
        log.debug("Stock réduit pour '{}': {} -> {}", product.getName(), product.getStock() + quantity, newStock);
    }

    private void restoreStock(Order order) {
        productRepository.findById(order.getProductId()).ifPresent(product -> {
            int restoredStock = product.getStock() + order.getQuantity();
            product.setStock(restoredStock);
            productRepository.save(product);
            log.debug("Stock restauré pour '{}': {}", product.getName(), restoredStock);
        });
    }

    private Order buildOrder(OrderRequestDTO request, Product product, double totalPrice) {
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setQuantity(request.quantity());
        order.setUnitPrice(product.getPrice());
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED.name());
        order.setCustomerEmail(request.customerEmail());
        order.setCustomerPhone(request.customerPhone());
        return order;
    }
}
