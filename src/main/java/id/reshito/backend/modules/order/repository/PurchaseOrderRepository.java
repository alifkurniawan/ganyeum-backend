package id.reshito.backend.modules.order.repository;

import id.reshito.backend.modules.order.model.PurchaseOrder;
import id.reshito.backend.modules.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByIdAndSeller_Id(UUID id, UUID sellerId);

    @Query(value = """
            select o from PurchaseOrder o
            where o.seller.id = :sellerId
              and (:status is null or o.status = :status)
              and (:fromDate is null or o.invoiceIssuedDate >= :fromDate)
              and (:untilDate is null or o.invoiceIssuedDate < :untilDate)
              and (:orderId is null or lower(o.orderId) = lower(:orderId))
              and (:invoiceId is null or lower(o.invoiceId) = lower(:invoiceId))
            """,
            countQuery = """
            select count(o) from PurchaseOrder o
            where o.seller.id = :sellerId
              and (:status is null or o.status = :status)
              and (:fromDate is null or o.invoiceIssuedDate >= :fromDate)
              and (:untilDate is null or o.invoiceIssuedDate < :untilDate)
              and (:orderId is null or lower(o.orderId) = lower(:orderId))
              and (:invoiceId is null or lower(o.invoiceId) = lower(:invoiceId))
            """)
    Page<PurchaseOrder> searchForSeller(
            @Param("sellerId") UUID sellerId,
            @Param("status") OrderStatus status,
            @Param("fromDate") Instant fromDate,
            @Param("untilDate") Instant untilDate,
            @Param("orderId") String orderId,
            @Param("invoiceId") String invoiceId,
            Pageable pageable
    );
}
