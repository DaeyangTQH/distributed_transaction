package com.haichan.service;

import com.haichan.dto.OrderItemRequest;
import com.haichan.dto.OrderItemResponse;
import com.haichan.dto.OrderRequest;
import com.haichan.dto.OrderResponse;
import com.haichan.entity.OrderItem;
import com.haichan.entity.OrderStatus;
import com.haichan.entity.StoreOrder;
import com.haichan.exception.BusinessException;
import com.haichan.exception.ForbiddenException;
import com.haichan.exception.ResourceNotFoundException;
import com.haichan.repository.StoreOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final StoreOrderRepository storeOrderRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return storeOrderRepository.findAllWithItems().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Integer id, Integer accountId) {
        StoreOrder order = findOrder(id);
        if (!order.getAccountId().equals(accountId)) {
            throw new ForbiddenException("You can only view your own order");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse create(Integer accountId, OrderRequest request) {
        StoreOrder order = StoreOrder.builder()
                .accountId(accountId)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            order.addItem(OrderItem.builder()
                    .watchId(itemRequest.getWatchId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(null)
                    .build());
        }

        return toResponse(storeOrderRepository.save(order));
    }

    /** Local status change only — saga/orchestrator will call this later. */
    @Transactional
    public OrderResponse confirm(Integer id) {
        StoreOrder order = findOrder(id);
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return toResponse(order);
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be confirmed");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        return toResponse(storeOrderRepository.save(order));
    }

    /** Local status change only — saga compensation will call this later. Idempotent. */
    @Transactional
    public OrderResponse cancel(Integer id) {
        StoreOrder order = findOrder(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return toResponse(order);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(storeOrderRepository.save(order));
    }

    private StoreOrder findOrder(Integer id) {
        return storeOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(StoreOrder order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .accountId(order.getAccountId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .watchId(item.getWatchId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}
