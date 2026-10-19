package id.reshito.backend.modules.customer;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(
        name = "uk_customers_seller_customer", columnNames = {"seller_id", "customer_id"}
))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Customer extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq")
    @SequenceGenerator(name = "customers_seq", sequenceName = "customers_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
}
