package id.reshito.backend.modules.inventory;

import id.reshito.backend.modules.inventory.models.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
