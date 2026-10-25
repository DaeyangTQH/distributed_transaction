package com.haichan.service;

import com.haichan.dto.PaymentRequest;
import com.haichan.dto.PaymentResponse;
import com.haichan.dto.PaymentScenario;
import com.haichan.dto.PaymentStatus;
import com.haichan.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentService {

    private static final long TIMEOUT_MS = 5000;

    private final AtomicInteger idSeq = new AtomicInteger(1);
    private final Map<Integer, PaymentResponse> payments = new ConcurrentHashMap<>();

    public PaymentResponse charge(PaymentRequest request) {
        PaymentScenario scenario = request.getScenario() == null
                ? PaymentScenario.SUCCESS
                : request.getScenario();

        if (scenario == PaymentScenario.TIMEOUT) {
            sleep();
        }

        PaymentStatus status = switch (scenario) {
            case SUCCESS -> PaymentStatus.SUCCESS;
            case FAIL -> PaymentStatus.FAILED;
            case TIMEOUT -> PaymentStatus.TIMEOUT;
        };

        PaymentResponse payment = PaymentResponse.builder()
                .paymentId(idSeq.getAndIncrement())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(status)
                .build();
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    public PaymentResponse getById(Integer id) {
        PaymentResponse payment = payments.get(id);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        return payment;
    }

    private void sleep() {
        try {
            Thread.sleep(TIMEOUT_MS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
