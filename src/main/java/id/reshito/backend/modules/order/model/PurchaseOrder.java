package id.reshito.backend.modules.order.model;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.user.User;
import id.reshito.backend.modules.shipping.model.Shipping;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders", indexes = {
        @Index(name = "idx_purchase_orders_seller_created", columnList = "seller_id, created_at"),
        @Index(name = "idx_purchase_orders_customer", columnList = "customer_id")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrder extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.WAITING_FOR_PACKING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone_number", nullable = false)
    private String customerPhoneNumber;

    @Column(name = "customer_address", nullable = false, columnDefinition = "text")
    private String customerAddress;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderDetail> products = new ArrayList<>();

    @Column(name = "total_price", nullable = false, precision = 38, scale = 0)
    private BigInteger totalPrice;

    @Column(name = "total_initial_cost", nullable = false, precision = 38, scale = 0)
    private BigInteger totalInitialCost;

    @Column(nullable = false, precision = 38, scale = 0)
    private BigInteger profit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;

    @Column(name = "shipping_name")
    private String shippingName;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShippingDetail shippingDetail;

    @Column(name = "invoice_id", nullable = false, unique = true)
    private String invoiceId;
    @Column(name = "invoice_issued_date", nullable = false)
    private Instant invoiceIssuedDate;
}
