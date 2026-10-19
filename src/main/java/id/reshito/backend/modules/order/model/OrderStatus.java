package id.reshito.backend.modules.order.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    READY_FOR_PICKUP,
    SHIPPED,
    COMPLETED,
    CANCELLED
}
