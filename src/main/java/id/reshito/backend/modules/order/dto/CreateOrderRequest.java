package id.reshito.backend.modules.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import id.reshito.backend.common.validation.ValidPhoneNumber;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        UUID customerId,
        @NotBlank @Size(max = 255) String customerName,
        @NotBlank @Size(max = 16) @ValidPhoneNumber String customerPhoneNumber,
        @NotBlank @Size(max = 2000) String customerAddress,
        @NotEmpty List<@Valid Item> items,
        @Positive Long shippingId,
        @DecimalMin("0") BigInteger shippingCost,
        @Size(max = 255) String trackingNumber
) {
    public record Item(@NotNull UUID productId, @NotNull @Positive Integer quantity) {}
}
