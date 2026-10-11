package id.reshito.backend.modules.product;


import id.reshito.backend.modules.product.dto.ProductResponse;
import id.reshito.backend.modules.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("""
            SELECT NEW id.reshito.backend.modules.product.dto.ProductResponse(
                i.id,
                i.productName,
                i.sellingPrice,
                i.initialPrice,
                COALESCE(SUM(inv.quantity), 0)
            )
            FROM Product i
            LEFT JOIN i.inventories inv
            WHERE i.uid = :uid AND i.active = true
              AND (
                  COALESCE(:productName, '') = ''
                  OR LOWER(i.productName) LIKE LOWER(CONCAT('%', :productName, '%'))
              )
            GROUP BY i.id, i.productName, i.sellingPrice, i.initialPrice
            """)
    Page<ProductResponse> findProductByProductNameAndUidWithQuantity(
            @Param("uid") String uid,
            @Param("productName") String productName,
            Pageable pageable
    );


    Optional<Product> findByIdAndUidAndActive(UUID productId, String uid, boolean active);
}
