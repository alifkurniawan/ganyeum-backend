package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.dto.StockQuantityRequest;
import id.reshito.backend.modules.inventory.dto.StockResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SELLER')")
public class InventoryController {
    private final StockService stockService;

    @PostMapping("/{productId}/add")
    public StockResponse addStock(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId,
                                  @Valid @RequestBody StockQuantityRequest request) {
        int quantity = stockService.addStockForSeller(jwt.getSubject(), productId, request.quantity());
        return new StockResponse(productId, quantity);
    }

    @PutMapping("/{productId}")
    public StockResponse setStock(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId,
                                  @Valid @RequestBody StockQuantityRequest request) {
        int quantity = stockService.setStockForSeller(jwt.getSubject(), productId, request.quantity());
        return new StockResponse(productId, quantity);
    }
}
