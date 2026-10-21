package id.reshito.backend.modules.order;

import id.reshito.backend.modules.order.dto.CreateOrderRequest;
import id.reshito.backend.modules.order.dto.PurchaseOrderResponse;
import id.reshito.backend.modules.order.dto.UpdateOrderStatusRequest;
import id.reshito.backend.modules.order.model.OrderStatus;
import id.reshito.backend.modules.order.dto.UpdatePaymentStatusRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SELLER')")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(jwt.getSubject(), request);
    }

    @GetMapping
    public id.reshito.backend.common.dto.ListResponse list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String invoiceId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return orderService.list(jwt.getSubject(), status, fromDate, toDate, orderId, invoiceId, page, size);
    }

    @GetMapping("/{orderId}")
    public PurchaseOrderResponse getDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID orderId
    ) {
        return orderService.getDetail(jwt.getSubject(), orderId);
    }

    @PatchMapping("/{orderId}/status")
    public PurchaseOrderResponse updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.updateStatus(jwt.getSubject(), orderId, request.status());
    }

    @PatchMapping("/{orderId}/payment-status")
    public PurchaseOrderResponse updatePaymentStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        return orderService.updatePaymentStatus(jwt.getSubject(), orderId, request.status());
    }
}
