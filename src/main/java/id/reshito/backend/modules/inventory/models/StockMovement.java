package id.reshito.backend.modules.inventory.models;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.item.model.Item;
import id.reshito.backend.modules.inventory.StockMovementType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "stock_movements")
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private int quantity;
    private StockMovementType stockMovementType;

    @Nullable
    private String reverenceType;

    @Nullable
    private String referenceId;
}
