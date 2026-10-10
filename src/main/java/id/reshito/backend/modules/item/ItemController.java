package id.reshito.backend.modules.item;

import id.reshito.backend.common.dto.GetRequest;
import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.dto.ItemUpdateRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(name="Item", path ="/api/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> addItem(
            @Valid @RequestBody ItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = itemService.addItem(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ListResponse> getItems(
            @Valid @ModelAttribute GetRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = itemService.getItems(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItems(
            @PathVariable UUID itemId,
            @Valid @RequestBody ItemUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        var response = itemService.editItem(userId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        itemService.deleteItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

}
