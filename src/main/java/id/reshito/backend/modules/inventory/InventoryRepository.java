package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByItemId(Long id);
}
