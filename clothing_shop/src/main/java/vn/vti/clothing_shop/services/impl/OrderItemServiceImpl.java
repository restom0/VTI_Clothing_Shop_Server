package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.OrderItem;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderItemMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.services.interfaces.OrderItemService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OnSaleProductRepository onSaleProductRepository;
    private final ImportedProductRepository importedProductRepository;
    private final PostgresToMongoReadModelSyncService readModelSyncService;

    @Cacheable(value = "orderItems", key = "'all'")
    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findByDeletedAtIsNullOrderByIdDesc()
                .stream()
                .map(orderItemMapper::entityToDTO)
                .toList();
    }

    @Cacheable(value = "orderItems", key = "'order:' + #orderId")
    @Override
    public List<OrderItemDTO> getAllOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByDeletedAtIsNullAndOrder_Id(orderId)
                .stream()
                .map(orderItemMapper::entityToDTO)
                .toList();
    }

    @Cacheable(value = "orderItems", key = "'id:' + #id + ':order:' + #orderId")
    @Override
    public OrderItemDTO findOrderItemByIdAndOrderId(Long id, Long orderId) throws WrapperException {
        try {
            return orderItemMapper.entityToDTO(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(id, orderId).orElseThrow(() -> new NotFoundException("messages.orderItems.notfound")));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    private Long calcSumProducts(List<ImportedProduct> products) {
        return products.stream().mapToLong(ImportedProduct::getStock).sum();
    }

    private List<ImportedProduct> findStockProducts(OnSaleProduct onSaleProduct) {
        return importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(
                NumberUtils.INTEGER_ZERO,
                onSaleProduct.getProduct().getId()
        );
    }

    private void reserveStock(List<ImportedProduct> products, Integer quantity) {
        int remainingQuantity = quantity;
        for (ImportedProduct product : products) {
            if (remainingQuantity <= 0) {
                break;
            }
            int reservedQuantity = Math.min(product.getStock(), remainingQuantity);
            product.setStock(product.getStock() - reservedQuantity);
            remainingQuantity -= reservedQuantity;
        }
        importedProductRepository.saveAll(products);
    }

    private void refundStock(OrderItem orderItem, Integer quantity) {
        ImportedProduct importedProduct = orderItem.getProduct().getProduct();
        int stockAfterRefund = Math.min(importedProduct.getImportNumber(), importedProduct.getStock() + quantity);
        importedProduct.setStock(stockAfterRefund);
        importedProductRepository.save(importedProduct);
    }

    private Long calculateLineTotal(Integer quantity, OnSaleProduct onSaleProduct) {
        double discount = onSaleProduct.getInputSale() == null || onSaleProduct.getInputSale().getDiscount() == null
                ? 0
                : onSaleProduct.getInputSale().getDiscount();
        return Math.round(quantity * onSaleProduct.getSalePrice() * (1 - discount / 100.0));
    }

    @Caching(evict = {
            @CacheEvict(value = "orderItems", allEntries = true),
            @CacheEvict(value = "orders", allEntries = true)
    })
    @Transactional
    @Override
    public void addOrderItem(OrderItemCreateRequest orderItemCreateRequest) throws WrapperException {
        try {
            Order order = orderRepository.findById(orderItemCreateRequest.orderId()).orElseThrow(() -> new NotFoundException("messages.orders.notfound"));
            OnSaleProduct onSaleProduct = onSaleProductRepository.findById(orderItemCreateRequest.productId()).orElseThrow(() -> new NotFoundException("messages.onSaleProducts.notfound"));
            List<ImportedProduct> importedProduct = findStockProducts(onSaleProduct);
            if (calcSumProducts(importedProduct) < orderItemCreateRequest.quantity()) {
                throw new BadRequestException("messages.stock.notEnough");
            }

            reserveStock(importedProduct, orderItemCreateRequest.quantity());
            OrderItem orderItem = orderItemRepository
                    .findByDeletedAtIsNullAndProduct_IdAndOrder_Id(onSaleProduct.getId(), orderItemCreateRequest.orderId())
                    .map(existing -> {
                        existing.setQuantity(existing.getQuantity() + orderItemCreateRequest.quantity());
                        return existing;
                    })
                    .orElseGet(() -> orderItemMapper.createRequestToEntity(orderItemCreateRequest, onSaleProduct, order));
            order.setTotalPrice(order.getTotalPrice() + calculateLineTotal(orderItemCreateRequest.quantity(), onSaleProduct));
            orderRepository.save(order);
            orderItemRepository.save(orderItem);
            readModelSyncService.syncAfterCommit(ReadModelType.ORDER, order.getId());
        } catch (NotFoundException | BadRequestException ex) {
            throw new WrapperException(ex);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "orderItems", allEntries = true),
            @CacheEvict(value = "orders", allEntries = true)
    })
    @Transactional
    @Override
    public void updateOrderItem(OrderItemUpdateRequest orderItemUpdateRequest, Long userId, Long orderId, Long orderItemId) throws WrapperException {
        try {
            Order order = orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(orderId, userId).orElseThrow(() -> new NotFoundException("messages.orders.notfound"));
            OrderItem orderItem = orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(orderItemId, order.getId()).orElseThrow(() -> new NotFoundException("messages.orderItems.notfound"));
            OnSaleProduct onSaleProduct = onSaleProductRepository.findByIdAndDeletedAtIsNull(orderItemUpdateRequest.productId()).orElseThrow(() -> new NotFoundException("messages.onSaleProducts.notfound"));
            boolean productChanged = !Objects.equals(orderItem.getProduct().getId(), onSaleProduct.getId());
            Long oldLineTotal = calculateLineTotal(orderItem.getQuantity(), orderItem.getProduct());
            Long newLineTotal = calculateLineTotal(orderItemUpdateRequest.quantity(), onSaleProduct);

            if (productChanged) {
                refundStock(orderItem, orderItem.getQuantity());
                List<ImportedProduct> importedProduct = findStockProducts(onSaleProduct);
                if (calcSumProducts(importedProduct) < orderItemUpdateRequest.quantity()) {
                    throw new BadRequestException("messages.stock.notEnough");
                }
                reserveStock(importedProduct, orderItemUpdateRequest.quantity());
            } else {
                int quantityDelta = orderItemUpdateRequest.quantity() - orderItem.getQuantity();
                if (quantityDelta > 0) {
                    List<ImportedProduct> importedProduct = findStockProducts(onSaleProduct);
                    if (calcSumProducts(importedProduct) < quantityDelta) {
                        throw new BadRequestException("messages.stock.notEnough");
                    }
                    reserveStock(importedProduct, quantityDelta);
                } else if (quantityDelta < 0) {
                    refundStock(orderItem, Math.abs(quantityDelta));
                }
            }

            order.setTotalPrice(order.getTotalPrice() - oldLineTotal + newLineTotal);
            orderRepository.save(order);
            orderItemRepository.save(orderItemMapper.updateRequestToEntity(orderItemUpdateRequest, onSaleProduct, orderItem));
            readModelSyncService.syncAfterCommit(ReadModelType.ORDER, order.getId());
        } catch (NotFoundException | BadRequestException ex) {
            throw new WrapperException(ex);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "orderItems", allEntries = true),
            @CacheEvict(value = "orders", allEntries = true)
    })
    @Transactional
    @Override
    public void deleteOrderItem(Long id, Long orderId) throws WrapperException {
        try {
            final OrderItem orderItem = orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(id, orderId)
                    .orElseThrow(() -> new NotFoundException("messages.orderItems.notfound"));
            final Order order = orderItem.getOrder();
            orderItem.setDeletedAt(Instant.now().toEpochMilli());
            refundStock(orderItem, orderItem.getQuantity());
            orderItemRepository.save(orderItem);
            order.setTotalPrice(Math.max(NumberUtils.LONG_ZERO, order.getTotalPrice() - calculateLineTotal(orderItem.getQuantity(), orderItem.getProduct())));
            orderRepository.save(order);
            readModelSyncService.syncAfterCommit(ReadModelType.ORDER, order.getId());
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }
}
