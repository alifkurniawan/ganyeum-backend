package id.reshito.backend.modules.order.model;

import id.reshito.backend.common.util.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigInteger;

@Entity
@Table(name = "shipping_details")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ShippingDetail extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipping_details_seq")
    @SequenceGenerator(name = "shipping_details_seq", sequenceName = "shipping_details_seq", allocationSize = 50)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false, unique = true)
    private PurchaseOrder purchaseOrder;

    @Column(name = "shipping_cost", nullable = false, precision = 38, scale = 0)
    private BigInteger shippingCost;

    @Column(name = "tracking_number")
    private String trackingNumber;
}
