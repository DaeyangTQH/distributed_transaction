package com.haichan.saga;

import com.haichan.client.WatchClient;
import com.haichan.dto.OrderItemResponse;
import com.haichan.dto.OrderRequest;
import com.haichan.dto.OrderResponse;
import com.haichan.dto.StockRequest;
import com.haichan.entity.SagaInstance;
import com.haichan.entity.SagaStepName;
import com.haichan.entity.SagaType;
import com.haichan.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaceOrderSaga {
    OrderService orderService;
    WatchClient watchClient;
    SagaLogService sagaLogService;

    public OrderResponse execute(Integer accountId, OrderRequest orderRequest) {
        OrderResponse order = orderService.create(accountId, orderRequest);
        SagaInstance saga = sagaLogService.start(order.getOrderId(), SagaType.PLACE_ORDER);
        sagaLogService.success(saga, SagaStepName.CREATE_ORDER, null, null, null);

        List<OrderItemResponse> reserved = new ArrayList<>();
        try {
            for (var item : order.getItems()) {
                String key = reserveKey(order.getOrderId(), item.getOrderItemId());
                try {
                    watchClient.reserve(item.getWatchId(), StockRequest.builder()
                            .idempotencyKey(key)
                            .quantity(item.getQuantity())
                            .build());
                    sagaLogService.success(saga, SagaStepName.RESERVE, item.getWatchId(), item.getQuantity(), key);
                    reserved.add(item);
                } catch (Exception reserveEx) {
                    sagaLogService.failed(saga, SagaStepName.RESERVE, item.getWatchId(), item.getQuantity(),
                            key, reserveEx.getMessage());
                    throw reserveEx;
                }
            }
            OrderResponse confirmed = orderService.confirm(order.getOrderId());
            sagaLogService.success(saga, SagaStepName.CONFIRM, null, null, null);
            sagaLogService.completed(saga);
            return confirmed;
        } catch (Exception ex) {
            log.error("PlaceOrderSaga failed for order {}: {}", order.getOrderId(), ex.getMessage());
            compensate(saga, order.getOrderId(), reserved, ex.getMessage());
            throw ex;
        }
    }

    private void compensate(SagaInstance saga, Integer orderId, List<OrderItemResponse> reserved, String errorMessage) {
        sagaLogService.compensating(saga, errorMessage);
        boolean restoreFailed = false;
        for (var item : reserved) {
            String key = restoreKey(orderId, item.getOrderItemId());
            try {
                watchClient.restore(item.getWatchId(), StockRequest.builder()
                        .idempotencyKey(key)
                        .quantity(item.getQuantity())
                        .build());
                sagaLogService.success(saga, SagaStepName.RESTORE, item.getWatchId(), item.getQuantity(), key);
            } catch (Exception restoreEx) {
                restoreFailed = true;
                sagaLogService.failed(saga, SagaStepName.RESTORE, item.getWatchId(), item.getQuantity(),
                        key, restoreEx.getMessage());
                log.error("Restore failed watch {} for order {}: {}",
                        item.getWatchId(), orderId, restoreEx.getMessage());
            }
        }
        orderService.cancel(orderId);
        sagaLogService.success(saga, SagaStepName.CANCEL_ORDER, null, null, null);
        if (restoreFailed) {
            sagaLogService.compensationFailed(saga);
        } else {
            sagaLogService.compensated(saga);
        }
    }

    private String reserveKey(Integer orderId, Integer orderItemId) {
        return "order-" + orderId + "-item-" + orderItemId + "-reserve";
    }

    private String restoreKey(Integer orderId, Integer orderItemId) {
        return "order-" + orderId + "-item-" + orderItemId + "-restore";
    }
}
