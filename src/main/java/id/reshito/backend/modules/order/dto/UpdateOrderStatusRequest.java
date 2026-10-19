package id.reshito.backend.modules.order.dto;

import id.reshito.backend.modules.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
