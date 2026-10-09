package id.reshito.backend.modules.item;

import id.reshito.backend.modules.inventory.InventoryService;
import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.mapper.ItemMapper;
import id.reshito.backend.modules.item.model.Item;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemResponse addItem(String uid, ItemRequest request) {
        Item item = Item.builder()
                .itemName(request.itemName())
                .initialPrice(request.initialPrice())
                .sellingPrice(request.sellingPrice())
                .uid(uid)
                .build();

        this.itemRepository.save(item);
        var inventory = this.inventoryService.addInventory(item, request.quantity());
        return itemMapper.toResponse(item, inventory.getQuantity());
    }
}
