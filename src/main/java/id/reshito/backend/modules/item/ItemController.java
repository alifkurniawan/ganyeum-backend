package id.reshito.backend.modules.item;

import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.mapper.ItemMapper;
import id.reshito.backend.modules.item.model.Item;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
