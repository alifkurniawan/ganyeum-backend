package id.reshito.backend.modules.item;


import id.reshito.backend.common.dto.GetRequest;
import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.common.mapper.ListMapper;
import id.reshito.backend.modules.inventory.InventoryService;
import id.reshito.backend.modules.item.dto.ItemRequest;
import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.dto.ItemUpdateRequest;
import id.reshito.backend.modules.item.mapper.ItemMapper;
import id.reshito.backend.modules.item.model.Item;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;
    private final ItemMapper itemMapper;
    private final ListMapper listMapper;

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

    public ListResponse getItems(String uid, GetRequest request) {
        var result = itemRepository.findItemByItemNameAndUidWithQuantity(
                uid, request.query(),
                PageRequest.of(
                        request.page() - 1,
                        request.size(),
                        Sort.by(request.sortBy()).ascending()
                )
        );

        return listMapper.toResponse(request.page(), result);
    }


    public ItemResponse editItem(String uid, UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findByIdAndUidAndActive(itemId, uid, true).orElseThrow(NotFoundException::new);
        item.setItemName(request.itemName());
        item.setInitialPrice(request.initialPrice());
        item.setSellingPrice(request.sellingPrice());
        this.itemRepository.save(item);
        var inventory = this.inventoryService.getInventory(item);
        return itemMapper.toResponse(item, inventory.getQuantity());
    }

    @Transactional
    public void deleteItem(String uid, UUID itemId) {
        Item item = itemRepository.findByIdAndUidAndActive(itemId, uid, true)
                .orElseThrow(NotFoundException::new);
        item.setActive(false);
        item.setDeletedAt(Instant.now());
        itemRepository.save(item);
    }
}
