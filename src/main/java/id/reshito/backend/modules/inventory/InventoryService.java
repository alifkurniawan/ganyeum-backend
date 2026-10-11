package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.models.Inventory;
import id.reshito.backend.modules.inventory.models.StockMovement;
import id.reshito.backend.modules.product.model.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public Inventory addInventory(Product product, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return this.updateInventory(product, quantity, StockMovementType.STOCK_IN);
    }

    @Transactional
    public Inventory decreaseInventory(Product product, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greather than zero");
        }
        return this.updateInventory(product, quantity*(-1), StockMovementType.SALE);
    }

    private Inventory updateInventory(Product product, Integer quantity, StockMovementType type) {
        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .quantity(0)
                        .build()
                );
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

        StockMovement stockMovement = StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .stockMovementType(type)
                .build();

        this.stockMovementRepository.save(stockMovement);
        return inventory;
    }

    public Inventory getInventory(Product product) {
        return inventoryRepository.findByProductId(product.getId()).orElseThrow();
    }
}
