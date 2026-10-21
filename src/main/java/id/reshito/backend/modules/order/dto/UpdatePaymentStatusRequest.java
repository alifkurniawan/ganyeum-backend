package id.reshito.backend.modules.order.dto;

import id.reshito.backend.modules.order.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(@NotNull PaymentStatus status) {}
