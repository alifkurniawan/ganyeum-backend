package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.models.Inventory;
import id.reshito.backend.modules.inventory.models.StockMovement;
import id.reshito.backend.modules.item.model.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public Inventory addInventory(Item item, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return this.updateInventory(item, quantity, StockMovementType.STOCK_IN);
    }

    @Transactional
    public Inventory decreaseInventory(Item item, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greather than zero");
        }
        return this.updateInventory(item, quantity*(-1), StockMovementType.SALE);
    }

    private Inventory updateInventory(Item item, Integer quantity, StockMovementType type) {
        Inventory inventory = inventoryRepository
                .findByItemId(item.getId())
                .orElseGet(() -> Inventory.builder()
                        .item(item)
                        .quantity(0)
                        .build()
                );
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

        StockMovement stockMovement = StockMovement.builder()
                .item(item)
                .quantity(quantity)
                .stockMovementType(type)
                .build();

        this.stockMovementRepository.save(stockMovement);
        return inventory;
    }
}
