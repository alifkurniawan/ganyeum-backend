package id.reshito.backend.modules.item;

import id.reshito.backend.modules.inventory.InventoryService;
import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.dto.ItemUpdateRequest;
import id.reshito.backend.modules.item.mapper.ItemMapper;
import id.reshito.backend.modules.item.model.Item;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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



    public ItemResponse editItem(String uid, UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findByIdAndUid(itemId, uid).orElseThrow();
        item.setItemName(request.itemName());
        item.setInitialPrice(request.initialPrice());
        item.setSellingPrice(request.sellingPrice());
        this.itemRepository.save(item);
        var inventory = this.inventoryService.getInventory(item);
        return itemMapper.toResponse(item, inventory.getQuantity());
    }
}
