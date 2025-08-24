package vn.vti.clothing_shop.services.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
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
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.services.interfaces.OrderItemService;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OnSaleProductRepository onSaleProductRepository;
    private final ImportedProductRepository importedProductRepository;

    //@Cacheable(value = "orderItems")
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findByDeletedAtIsNullOrderByIdDesc()
                .stream()
                .map(orderItemMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "orderItems", key = "#orderId")
    public List<OrderItemDTO> getAllOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByDeletedAtIsNullAndOrder_Id(orderId)
                .stream()
                .map(orderItemMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "orderItems", key = "#id,#orderId")
    public OrderItemDTO findOrderItemByIdAndOrderId(Long id, Long orderId) throws WrapperException {
        try {
            return orderItemMapper.entityToDTO(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(id, orderId).orElseThrow(() -> new NotFoundException("OrderItem not found")));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    private Long calcSumProducts(List<ImportedProduct> products) {
        return products.stream().mapToLong(ImportedProduct::getStock).sum();
    }

    //@CacheEvict(value = "orderItems", allEntries = true)
    public void addOrderItem(OrderItemCreateRequest orderItemCreateRequest) throws WrapperException {
        try {
            Order order = orderRepository.findById(orderItemCreateRequest.orderId()).orElseThrow(() -> new NotFoundException("Order not found"));
            OnSaleProduct onSaleProduct = onSaleProductRepository.findById(orderItemCreateRequest.productId()).orElseThrow(() -> new NotFoundException("OnSaleProduct not found"));
            List<ImportedProduct> importedProduct = importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(onSaleProduct.getProduct().getId(), NumberUtils.INTEGER_ZERO);
            if (calcSumProducts(importedProduct) < orderItemCreateRequest.quantity()) {
                throw new BadRequestException("Not enough stock");
            }

            AtomicReference<Integer> totalQuantity = new AtomicReference<>(0);
            importedProduct.forEach(product -> {
                totalQuantity.updateAndGet(v -> v + product.getStock());
                if (totalQuantity.get() >= orderItemCreateRequest.quantity()) {
                    product.setStock(totalQuantity.get() - orderItemCreateRequest.quantity());
                } else {
                    product.setStock(NumberUtils.INTEGER_ZERO);
                }

            });
            importedProductRepository.saveAll(importedProduct);
            OrderItem orderItem = orderItemRepository
                    .findByDeletedAtIsNullAndIdAndOrder_Id(order.getId(), orderItemCreateRequest.orderId())
                    .map(existing -> {
                        existing.setQuantity(existing.getQuantity() + orderItemCreateRequest.quantity());
                        return existing;
                    })
                    .orElseGet(() -> orderItemMapper.createRequestToEntity(onSaleProduct, order));
            order.setTotalPrice(order.getTotalPrice() +
                    orderItemCreateRequest.quantity() *
                            orderItem.getProduct().getSalePrice() *
                            (1 - orderItem.getProduct().getInputSale().getDiscount() / 100));
            orderRepository.save(order);
            orderItemRepository.save(orderItem);
        } catch (NotFoundException | BadRequestException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "orderItems")
    public void updateOrderItem(OrderItemUpdateRequest orderItemUpdateRequest, Long userId, Long orderId, Long orderItemId) throws WrapperException {
        try {
            Order order = orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(orderId, userId).orElseThrow(() -> new NotFoundException("Order not found"));
            OrderItem orderItem = orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(orderItemId, order.getId()).orElseThrow(() -> new NotFoundException("OrderItem not found"));
            List<ImportedProduct> importedProduct = importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(orderItemUpdateRequest.productId(), NumberUtils.INTEGER_ZERO);
            OnSaleProduct onSaleProduct = onSaleProductRepository.findByIdAndDeletedAtIsNull(orderItemUpdateRequest.productId()).orElseThrow(() -> new NotFoundException("OnSaleProduct not found"));

            if (calcSumProducts(importedProduct) < orderItemUpdateRequest.quantity()) {
                throw new BadRequestException("Not enough stock");
            }
            if (orderItem.getQuantity() >= orderItemUpdateRequest.quantity()) {
                AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItem.getQuantity() - orderItemUpdateRequest.quantity());
                if (importedProduct.get(0).getStock() + orderItem.getQuantity() - orderItemUpdateRequest.quantity() > importedProduct.get(0).getImportNumber()) {
                    importedProduct.get(0).setStock(importedProduct.get(0).getImportNumber());
                    importedProductRepository.save(importedProduct.get(0));
                    List<ImportedProduct> zeroStockProduct = importedProductRepository.findByDeletedAtIsNullAndStockAndIdOrderByCreatedAtAsc(NumberUtils.INTEGER_ZERO, orderItem.getProduct().getId());
                    zeroStockProduct.forEach(product -> {
                        if (refundQuantity.get() > 0) {
                            if (product.getStock() + refundQuantity.get() <= product.getImportNumber()) {
                                product.setStock(product.getStock() + refundQuantity.get());
                                importedProductRepository.save(product);
                                refundQuantity.set(0);
                            } else {
                                refundQuantity.updateAndGet(v -> v - product.getImportNumber() - product.getStock());
                                product.setStock(product.getImportNumber());
                                importedProductRepository.save(product);
                            }
                        }
                    });
                } else {
                    importedProduct.get(0).setStock(importedProduct.get(0).getStock() + orderItem.getQuantity() - orderItemUpdateRequest.quantity());
                    importedProductRepository.save(importedProduct.get(0));
                }
            } else {
                AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItemUpdateRequest.quantity() - orderItem.getQuantity());
                importedProduct.forEach(product -> {
                    if (product.getStock() >= refundQuantity.get()) {
                        product.setStock(product.getStock() - refundQuantity.get());
                        importedProductRepository.save(product);
                        refundQuantity.set(0);
                    } else {
                        refundQuantity.updateAndGet(v -> v - product.getStock());
                        product.setStock(0);

                        importedProductRepository.save(product);
                    }
                });
            }
            order.setTotalPrice(order.getTotalPrice() + (orderItemUpdateRequest.quantity() - orderItem.getQuantity()) * orderItem.getProduct().getSalePrice());
            orderItemRepository.save(orderItemMapper.updateRequestToEntity(orderItemUpdateRequest, onSaleProduct, orderItem));
        } catch (NotFoundException | BadRequestException ex) {
            throw new WrapperException(ex);
        }
    }

    private void refundStock(OrderItem orderItem) {
        List<ImportedProduct> importedProduct = importedProductRepository.findByDeletedAtIsNullAndStockAndIdOrderByCreatedAtAsc(NumberUtils.INTEGER_ZERO, orderItem.getProduct().getId());
        AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItem.getQuantity());
        importedProduct.forEach(product -> {
            if (product.getStock() + refundQuantity.get() <= product.getImportNumber()) {
                product.setStock(product.getStock() + refundQuantity.get());
                importedProductRepository.save(product);
            } else {
                refundQuantity.updateAndGet(v -> v - product.getImportNumber() - product.getStock());
                product.setStock(product.getImportNumber());
                importedProductRepository.save(product);
            }
        });
    }

    //@CacheEvict(value = "orderItems", allEntries = true)
    public void deleteOrderItem(Long id, Long orderId) throws WrapperException {
        try {
            final Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
            final List<OrderItem> orderItems = orderItemRepository.findByDeletedAtIsNullAndOrder_Id(orderId);

            orderItems.forEach(orderItem -> {
                orderItem.setDeletedAt(Instant.now().toEpochMilli());
                refundStock(orderItem);
                orderItemRepository.save(orderItem);
            });
            order.setTotalPrice(order.getTotalPrice() - orderItems.stream().mapToLong(orderItem -> (orderItem.getQuantity() * orderItem.getProduct().getSalePrice())).sum());

        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }
}
