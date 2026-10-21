package id.reshito.backend.modules.inventory;

import id.reshito.backend.common.exception.BadRequestException;
import id.reshito.backend.modules.inventory.models.Inventory;
import id.reshito.backend.modules.inventory.models.StockMovement;
import id.reshito.backend.modules.product.ProductLookupService;
import id.reshito.backend.modules.product.model.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductLookupService productLookupService;

    @Transactional
    public Integer addStockForSeller(String sellerUid, UUID productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        Product product = productLookupService.findActiveForSeller(sellerUid, productId);
        return addStock(product, quantity, "MANUAL_STOCK_IN", productId.toString()).getQuantity();
    }

    @Transactional
    public Integer setStockForSeller(String sellerUid, UUID productId, Integer quantity) {
        if (quantity == null) {
            throw new BadRequestException("Quantity is required");
        }
        Product product = productLookupService.findActiveForSeller(sellerUid, productId);
        return adjustStock(product, quantity).getQuantity();
    }

    @Transactional
    public Integer initializeStock(Product product, Integer quantity) {
        return addStock(product, quantity, "INITIAL_STOCK", product.getId().toString()).getQuantity();
    }

    @Transactional
    public Integer decreaseStock(Product product, Integer quantity, String referenceType, String referenceId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        return updateStock(product, -quantity, StockMovementType.SALE, referenceType, referenceId).getQuantity();
    }

    @Transactional
    public Integer returnStock(Product product, Integer quantity, String orderId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        return updateStock(product, quantity, StockMovementType.RETURN, "CANCELLED_ORDER", orderId).getQuantity();
    }

    @Transactional
    public Integer getStock(Product product) {
        return inventoryRepository.findByProductId(product.getId())
                .map(Inventory::getQuantity).orElse(0);
    }

    private Inventory addStock(Product product, Integer quantity, String referenceType, String referenceId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        return updateStock(product, quantity, StockMovementType.STOCK_IN, referenceType, referenceId);
    }

    private Inventory adjustStock(Product product, Integer targetQuantity) {
        if (targetQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
        Inventory inventory = findOrCreateInventory(product);
        int delta = targetQuantity - inventory.getQuantity();
        if (delta != 0) {
            inventory.setQuantity(targetQuantity);
            inventoryRepository.saveAndFlush(inventory);
            saveMovement(product, delta, StockMovementType.ADJUSTMENT,
                    "MANUAL_ADJUSTMENT", product.getId().toString());
        } else if (inventory.getId() == null) {
            inventoryRepository.saveAndFlush(inventory);
        }
        return inventory;
    }

    private Inventory updateStock(Product product, int delta, StockMovementType movementType,
                                  String referenceType, String referenceId) {
        Inventory inventory = findOrCreateInventory(product);
        inventory.setQuantity(inventory.getQuantity() + delta);
        inventoryRepository.saveAndFlush(inventory);
        saveMovement(product, delta, movementType, referenceType, referenceId);
        return inventory;
    }

    private Inventory findOrCreateInventory(Product product) {
        return inventoryRepository.findByProductId(product.getId())
                .orElseGet(() -> Inventory.builder().product(product).quantity(0).build());
    }

    private void saveMovement(Product product, int quantity, StockMovementType movementType,
                              String referenceType, String referenceId) {
        stockMovementRepository.save(StockMovement.builder().product(product).quantity(quantity)
                .stockMovementType(movementType).reverenceType(referenceType).referenceId(referenceId).build());
    }
}
