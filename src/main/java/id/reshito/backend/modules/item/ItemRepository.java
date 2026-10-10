package id.reshito.backend.modules.item;


import id.reshito.backend.modules.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByIdAndUid(UUID itemId, String uid);
    Page<Item> findByItemNameContainingIgnoreCase(String itemName, Pageable pageable);
}
