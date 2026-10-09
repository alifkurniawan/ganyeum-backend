package id.reshito.backend.modules.item;

import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

//    @PostMapping
//    public ResponseEntity<Void> addItem(@RequestBody ItemRequest request) {
//
//    }


}
