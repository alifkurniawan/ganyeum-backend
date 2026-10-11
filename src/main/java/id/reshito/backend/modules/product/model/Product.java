package id.reshito.backend.modules.product.model;


import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.inventory.models.Inventory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "products",
        indexes = {@Index(name="idx_products_uid_item_name", columnList = "uid, product_name")}
)
public class Product extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String uid;

    private String productName;
    private BigInteger sellingPrice;
    private BigInteger initialPrice;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Inventory> inventories;


    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

}
