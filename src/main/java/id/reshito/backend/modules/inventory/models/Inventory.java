package id.reshito.backend.modules.inventory.models;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "inventories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Inventory extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    @Version
    private int version;
}
