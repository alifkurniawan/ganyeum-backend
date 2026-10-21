package id.reshito.backend.modules.order.model;

public enum OrderStatus {
    WAITING_FOR_PACKING,
    READY_TO_DELIVERY,
    ON_DELIVERY,
    FINISH,
    CANCELLED
}
