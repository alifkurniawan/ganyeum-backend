package id.reshito.backend.modules.order.model;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigInteger;

@Entity
@Table(name = "purchase_order_details")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderDetail extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_details_seq")
    @SequenceGenerator(name = "purchase_order_details_seq", sequenceName = "purchase_order_details_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "price_per_item", nullable = false, precision = 38, scale = 0)
    private BigInteger pricePerItem;

    @Column(name = "initial_cost_per_item", nullable = false, precision = 38, scale = 0)
    private BigInteger initialCostPerItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 38, scale = 0)
    private BigInteger totalPrice;

    @Column(name = "total_initial_cost", nullable = false, precision = 38, scale = 0)
    private BigInteger totalInitialCost;
}
