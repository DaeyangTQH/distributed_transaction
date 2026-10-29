package com.haichan.saga;

import com.haichan.client.WatchClient;
import com.haichan.dto.OrderResponse;
import com.haichan.dto.OrderRequest;
import com.haichan.dto.StockBatchRequest;
import com.haichan.dto.StockItemRequest;
import com.haichan.entity.SagaInstance;
import com.haichan.entity.SagaStepName;
import com.haichan.entity.SagaType;
import com.haichan.exception.BusinessException;
import com.haichan.exception.ResourceNotFoundException;
import com.haichan.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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

        String reserveKey = reserveKey(order.getOrderId());
        StockBatchRequest batch = toBatch(reserveKey, order);
        boolean stockReserved = false;
        try {
            log.info("RESERVE-BATCH: sagaId={}, orderId={}, items={}, key={}",
                    saga.getSagaId(), order.getOrderId(), order.getItems().size(), reserveKey);
            watchClient.reserveBatch(batch);
            stockReserved = true;
            sagaLogService.success(saga, SagaStepName.RESERVE, null, order.getItems().size(), reserveKey);

            OrderResponse confirmed = orderService.confirm(order.getOrderId());
            sagaLogService.success(saga, SagaStepName.CONFIRM, null, null, null);
            sagaLogService.completed(saga);
            return confirmed;
        } catch (ResourceNotFoundException | BusinessException ex) {
            log.warn("PlaceOrderSaga FAILED (business): sagaId={}, orderId={}, stockReserved={}, reason={}",
                    saga.getSagaId(), order.getOrderId(), stockReserved, ex.getMessage());
            failAndCompensate(saga, order, batch, stockReserved, reserveKey, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("PlaceOrderSaga FAILED: sagaId={}, orderId={}, stockReserved={}, reason={}",
                    saga.getSagaId(), order.getOrderId(), stockReserved, ex.getMessage(), ex);
            failAndCompensate(saga, order, batch, stockReserved, reserveKey, ex.getMessage());
            throw ex;
        }
    }

    private void failAndCompensate(
            SagaInstance saga,
            OrderResponse order,
            StockBatchRequest batch,
            boolean stockReserved,
            String reserveKey,
            String errorMessage) {
        if (!stockReserved) {
            sagaLogService.failed(saga, SagaStepName.RESERVE, null, order.getItems().size(),
                    reserveKey, errorMessage);
        }
        compensate(saga, order, batch, stockReserved, errorMessage);
    }

    private void compensate(
            SagaInstance saga,
            OrderResponse order,
            StockBatchRequest reserveBatch,
            boolean stockReserved,
            String errorMessage) {
        sagaLogService.compensating(saga, errorMessage);
        if (stockReserved) {
            String restoreKey = restoreKey(order.getOrderId());
            try {
                log.warn("RESTORE-BATCH: sagaId={}, orderId={}, key={}",
                        saga.getSagaId(), order.getOrderId(), restoreKey);
                watchClient.restoreBatch(StockBatchRequest.builder()
                        .idempotencyKey(restoreKey)
                        .items(reserveBatch.getItems())
                        .build());
                sagaLogService.success(saga, SagaStepName.RESTORE, null, order.getItems().size(), restoreKey);
            } catch (Exception restoreEx) {
                sagaLogService.failed(saga, SagaStepName.RESTORE, null, order.getItems().size(),
                        restoreKey, restoreEx.getMessage());
                log.error("RESTORE-BATCH failed: sagaId={}, orderId={}, reason={}",
                        saga.getSagaId(), order.getOrderId(), restoreEx.getMessage(), restoreEx);
                orderService.cancel(order.getOrderId());
                sagaLogService.success(saga, SagaStepName.CANCEL_ORDER, null, null, null);
                sagaLogService.compensationFailed(saga);
                return;
            }
        }
        orderService.cancel(order.getOrderId());
        sagaLogService.success(saga, SagaStepName.CANCEL_ORDER, null, null, null);
        sagaLogService.compensated(saga);
        log.warn("COMPENSATE finished COMPENSATED: sagaId={}, orderId={}, restoredStock={}",
                saga.getSagaId(), order.getOrderId(), stockReserved);
    }

    private StockBatchRequest toBatch(String key, OrderResponse order) {
        List<StockItemRequest> items = order.getItems().stream()
                .map(item -> StockItemRequest.builder()
                        .watchId(item.getWatchId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();
        return StockBatchRequest.builder()
                .idempotencyKey(key)
                .items(items)
                .build();
    }

    private String reserveKey(Integer orderId) {
        return "order-" + orderId + "-reserve";
    }

    private String restoreKey(Integer orderId) {
        return "order-" + orderId + "-restore";
    }
}
