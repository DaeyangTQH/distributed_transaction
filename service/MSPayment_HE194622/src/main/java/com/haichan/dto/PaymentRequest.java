package com.haichan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "OrderID is required")
    private Integer orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be > 0")
    private BigDecimal amount;

    /** SUCCESS (default), FAIL, TIMEOUT */
    private PaymentScenario scenario = PaymentScenario.SUCCESS;
}
