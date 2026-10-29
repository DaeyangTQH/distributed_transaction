package com.haichan.saga;

import com.haichan.entity.*;
import com.haichan.repository.SagaInstanceRepository;
import com.haichan.repository.SagaStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SagaLogService {

    private static final int ERROR_MAX = 500;

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;

    @Transactional
    public SagaInstance start(Integer orderId, SagaType type) {
        return sagaInstanceRepository.save(SagaInstance.builder()
                .orderId(orderId)
                .type(type)
                .status(SagaInstanceStatus.STARTED)
                .build());
    }

    @Transactional
    public void success(SagaInstance saga, SagaStepName name, Integer watchId, Integer quantity, String idempotencyKey) {
        saveStep(saga, name, SagaStepStatus.SUCCESS, watchId, quantity, idempotencyKey, null);
    }

    @Transactional
    public void failed(SagaInstance saga, SagaStepName name, Integer watchId, Integer quantity,
                       String idempotencyKey, String errorMessage) {
        saveStep(saga, name, SagaStepStatus.FAILED, watchId, quantity, idempotencyKey, errorMessage);
        saga.setErrorMessage(truncate(errorMessage));
        sagaInstanceRepository.save(saga);
    }

    @Transactional
    public void completed(SagaInstance saga) {
        saga.setStatus(SagaInstanceStatus.COMPLETED);
        saga.setErrorMessage(null);
        sagaInstanceRepository.save(saga);
    }

    @Transactional
    public void compensating(SagaInstance saga, String errorMessage) {
        saga.setStatus(SagaInstanceStatus.COMPENSATING);
        saga.setErrorMessage(truncate(errorMessage));
        sagaInstanceRepository.save(saga);
    }

    @Transactional
    public void compensated(SagaInstance saga) {
        saga.setStatus(SagaInstanceStatus.COMPENSATED);
        sagaInstanceRepository.save(saga);
    }

    @Transactional
    public void compensationFailed(SagaInstance saga) {
        saga.setStatus(SagaInstanceStatus.COMPENSATION_FAILED);
        sagaInstanceRepository.save(saga);
    }

    private void saveStep(SagaInstance saga, SagaStepName name, SagaStepStatus status,
                          Integer watchId, Integer quantity, String idempotencyKey, String errorMessage) {
        sagaStepRepository.save(SagaStep.builder()
                .saga(saga)
                .name(name)
                .status(status)
                .watchId(watchId)
                .quantity(quantity)
                .idempotencyKey(idempotencyKey)
                .errorMessage(truncate(errorMessage))
                .build());
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() <= ERROR_MAX ? message : message.substring(0, ERROR_MAX);
    }
}
