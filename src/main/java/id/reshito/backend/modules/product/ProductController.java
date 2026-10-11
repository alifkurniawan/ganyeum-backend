package id.reshito.backend.modules.product;

import id.reshito.backend.common.dto.GetRequest;
import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.modules.product.dto.ProductRequest;
import id.reshito.backend.modules.product.dto.ProductResponse;
import id.reshito.backend.modules.product.dto.ProductUpdateRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(name="Item", path ="/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> addItem(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = productService.addProduct(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ListResponse> getItems(
            @Valid @ModelAttribute GetRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = productService.getProducts(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ProductResponse> updateItems(
            @PathVariable UUID itemId,
            @Valid @RequestBody ProductUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = productService.updateProduct(userId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        productService.deleteProduct(userId, itemId);
        return ResponseEntity.noContent().build();
    }

}
