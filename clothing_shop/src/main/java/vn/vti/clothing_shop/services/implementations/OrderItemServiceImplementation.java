package vn.vti.clothing_shop.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.entities.*;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.mappers.OrderItemMapper;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.services.interfaces.OrderItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderItemServiceImplementation implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OnSaleProductRepository onSaleProductRepository;
    private final ImportedProductRepository importedProductRepository;

    @Autowired
    public OrderItemServiceImplementation(OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper, OrderRepository orderRepository, OnSaleProductRepository onSaleProductRepository, ImportedProductRepository importedProductRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.orderRepository = orderRepository;
        this.onSaleProductRepository = onSaleProductRepository;
        this.importedProductRepository = importedProductRepository;
    }

    //@Cacheable(value = "orderItems")
    public List<OrderItemDTO> getAllOrderItem(){
        return orderItemMapper.ListEntityToListDTO(orderItemRepository.findAll());
    };

    //@Cacheable(value = "orderItems", key = "#orderId")
    public List<OrderItemDTO> getAllOrderItemsByOrderId(Long orderId){
        return orderItemMapper.ListEntityToListDTO(orderItemRepository.findAllByOrderId(orderId));
    };

    //@Cacheable(value = "orderItems", key = "#id,#orderId")
    public OrderItemDTO getOrderItemByIdAndOrderId(Long id,Long orderId){
        return orderItemMapper.EntityToDTO(orderItemRepository.findByIdAndOrderId(id,orderId).orElseThrow(()-> new NotFoundException("OrderItem not found")));
    };

    private Long calcSumProducts(List<ImportedProduct> products){
        return products.stream().mapToLong(ImportedProduct::getStock).sum();
    }

    //@CacheEvict(value = "orderItems", allEntries = true)
    public Boolean addOrderItem(OrderItemCreateDTO orderItemCreateDTO){
        Order order = orderRepository.findById(orderItemCreateDTO.getOrder_id()).orElseThrow(()-> new NotFoundException("Order not found"));
        OnSaleProduct onSaleProduct = onSaleProductRepository.findById(orderItemCreateDTO.getProduct_id()).orElseThrow(()-> new NotFoundException("OnSaleProduct not found"));
        List<ImportedProduct> importedProduct = importedProductRepository.findByProductIdWithPositiveStock(onSaleProduct.getProduct_id().getId());
        if(calcSumProducts(importedProduct) < orderItemCreateDTO.getQuantity()){
            throw new BadRequestException("Not enough stock");
        }

        importedProduct.forEach(product -> {
            if(product.getStock() >= orderItemCreateDTO.getQuantity()){
                product.setStock(product.getStock() - orderItemCreateDTO.getQuantity());
                importedProductRepository.save(product);
            }else{
                orderItemCreateDTO.setQuantity(orderItemCreateDTO.getQuantity() - product.getStock());
                product.setStock(0);
                importedProductRepository.save(product);
            }
        });
        if(orderItemRepository.findByIdAndOrderId(order.getId(),orderItemCreateDTO.getOrder_id()).isPresent()){
            OrderItem orderItem = orderItemRepository.findByIdAndOrderId(order.getId(),orderItemCreateDTO.getOrder_id()).get();
            orderItem.setQuantity(orderItem.getQuantity() + orderItemCreateDTO.getQuantity());
            order.setTotal_price((long) (
                    order.getTotal_price() +
                            orderItemCreateDTO.getQuantity() *
                                    orderItem.getProduct_id().getSale_price()*
                                    (1-orderItem.getProduct_id().getDiscount()/100)));
            orderRepository.save(order);
            orderItemRepository.save(orderItem);
            return true;
        }
        OrderItem orderItem = orderItemMapper.CreateDTOToEntity(orderItemCreateDTO,order,onSaleProduct);
        orderItemRepository.save(orderItem);
        order.setTotal_price((long) (
                order.getTotal_price() +
                        orderItemCreateDTO.getQuantity() *
                                orderItem.getProduct_id().getSale_price()*
                                (1-orderItem.getProduct_id().getDiscount()/100)));
        orderRepository.save(order);
        return true;
    };

    //@CachePut(value = "orderItems")
    public Boolean updateOrderItem(OrderItemUpdateDTO orderItemUpdateDTO){
        Order order = orderRepository.findOrderByIdAndUserId(orderItemUpdateDTO.getOrder_id(),orderItemUpdateDTO.getUser_id()).orElseThrow(()-> new NotFoundException("Order not found"));
        OrderItem orderItem = this.orderItemRepository.findByIdAndOrderId(orderItemUpdateDTO.getId(),order.getId()).orElseThrow(()-> new NotFoundException("OrderItem not found"));
        List<ImportedProduct> importedProduct = importedProductRepository.findByProductIdWithPositiveStock(orderItem.getProduct_id().getId());

        if(calcSumProducts(importedProduct) < orderItemUpdateDTO.getQuantity()){
            throw new BadRequestException("Not enough stock");
        }
        if(orderItem.getQuantity()>=orderItemUpdateDTO.getQuantity()){
            AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItem.getQuantity() - orderItemUpdateDTO.getQuantity());
            if(importedProduct.get(0).getStock() + orderItem.getQuantity() - orderItemUpdateDTO.getQuantity() > importedProduct.get(0).getImportNumber()) {
                importedProduct.get(0).setStock(importedProduct.get(0).getImportNumber());
                importedProductRepository.save(importedProduct.get(0));
                List<ImportedProduct> ZeroStockProduct = importedProductRepository.findByProductIdWithZeroStock(orderItem.getProduct_id().getId());
                ZeroStockProduct.forEach(product -> {
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
            }
            else {
                importedProduct.get(0).setStock(importedProduct.get(0).getStock() + orderItem.getQuantity() - orderItemUpdateDTO.getQuantity());
                importedProductRepository.save(importedProduct.get(0));
            }
        }
        else {
            AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItemUpdateDTO.getQuantity() - orderItem.getQuantity());
            importedProduct.forEach(product -> {
                if(product.getStock() >= refundQuantity.get()){
                    product.setStock(product.getStock() - refundQuantity.get());
                    importedProductRepository.save(product);
                    refundQuantity.set(0);
                }else{
                    refundQuantity.updateAndGet(v -> v - product.getStock());
                    product.setStock(0);

                    importedProductRepository.save(product);
                }
            });
        }
//        importedProduct.forEach(product -> {
//            if(orderItem.getQuantity()>=orderItemUpdateDTO.getQuantity()){
//                product.setStock(product.getStock() + orderItem.getQuantity() - orderItemUpdateDTO.getQuantity());
//                importedProductRepository.save(product);
//            }else{
//
//            }
//        });
        order.setTotal_price((long) (order.getTotal_price() + (orderItemUpdateDTO.getQuantity() - orderItem.getQuantity()) * orderItem.getProduct_id().getSale_price()));
        this.orderItemRepository.save(orderItemMapper.UpdateDTOToEntity(orderItemUpdateDTO,orderItem));
        return true;
    };
    private void refundStock(OrderItem orderItem){
        List<ImportedProduct> importedProduct = importedProductRepository.findByProductIdWithZeroStock(orderItem.getProduct_id().getId());
        AtomicReference<Integer> refundQuantity = new AtomicReference<>(orderItem.getQuantity());
        importedProduct.forEach(product -> {
            if(product.getStock() + refundQuantity.get() <= product.getImportNumber()){
                product.setStock(product.getStock() + refundQuantity.get());
                importedProductRepository.save(product);
            }else{
                refundQuantity.updateAndGet(v -> v - product.getImportNumber() - product.getStock());
                product.setStock(product.getImportNumber());
                importedProductRepository.save(product);
            }
        });
    }

    //@CacheEvict(value = "orderItems", allEntries = true)
    public Boolean deleteOrderItem(Long id,Long orderId){
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(id);
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new NotFoundException("Order not found"));
        orderItems.forEach((orderItem)->{
            orderItem.setDeleted_at(LocalDateTime.now());
            this.refundStock(orderItem);
            this.orderItemRepository.save(orderItem);
        });
        order.setTotal_price(order.getTotal_price() - orderItems.stream().mapToLong(orderItem -> (long) (orderItem.getQuantity() * orderItem.getProduct_id().getSale_price())).sum());
        return true;
    };
}
