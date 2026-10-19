package id.reshito.backend.modules.order.dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import id.reshito.backend.modules.order.model.OrderStatus;

public record PurchaseOrderResponse(
        UUID id,
        String orderId,
        OrderStatus status,
        UUID customerId,
        String customerName,
        String customerPhoneNumber,
        String customerAddress,
        List<Item> products,
        BigInteger subtotal,
        BigInteger totalInitialCost,
        BigInteger profit,
        BigInteger shippingCost,
        BigInteger totalPrice,
        Long shippingId,
        String shippingName,
        String trackingNumber,
        String invoiceId,
        Instant invoiceIssuedDate
) {
    public record Item(UUID productId, String productName, BigInteger pricePerItem,
                       BigInteger initialCostPerItem, Integer quantity,
                       BigInteger totalPrice, BigInteger totalInitialCost) {}
}
