package id.reshito.backend.modules.item;


import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    @Query("""
            SELECT NEW id.reshito.backend.modules.item.dto.ItemResponse(
                i.id,
                i.itemName,
                i.sellingPrice,
                i.initialPrice,
                COALESCE(SUM(inv.quantity), 0)
            )
            FROM Item i
            LEFT JOIN i.inventories inv
            WHERE i.uid = :uid AND i.active = true
              AND (
                  COALESCE(:itemName, '') = ''
                  OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :itemName, '%'))
              )
            GROUP BY i.id, i.itemName, i.sellingPrice, i.initialPrice
            """)
    Page<ItemResponse> findItemByItemNameAndUidWithQuantity(
            @Param("uid") String uid,
            @Param("itemName") String itemName,
            Pageable pageable
    );


    Optional<Item> findByIdAndUidAndActive(UUID itemId, String uid, boolean active);
}
