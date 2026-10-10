package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository
        extends JpaRepository<Inventory, UUID>,
        JpaSpecificationExecutor<Inventory> {
    Optional<Inventory> findByItemId(UUID id);
}
