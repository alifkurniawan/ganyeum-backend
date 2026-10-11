package id.reshito.backend.modules.product;

import id.reshito.backend.common.dto.GetRequest;
import id.reshito.backend.common.dto.ListResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping(name="Item", path ="/api/catalogue/{sellerId}")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('BUYER')")
public class CatalogController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ListResponse> getProduct(
            @PathVariable UUID sellerId,
            @Valid @ModelAttribute GetRequest request

    ) {
        ListResponse response = productService.getProductsBySeller(sellerId, request);
        return ResponseEntity.ok(response);
    }
}
