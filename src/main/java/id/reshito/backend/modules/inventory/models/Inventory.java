package id.reshito.backend.modules.inventory.models;

import id.reshito.backend.common.util.BaseModel;
import id.reshito.backend.modules.item.model.Item;
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
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Integer quantity;

    @Version
    private int version;
}
