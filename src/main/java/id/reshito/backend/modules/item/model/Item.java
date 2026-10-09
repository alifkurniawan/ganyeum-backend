package id.reshito.backend.modules.item.model;


import id.reshito.backend.common.util.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "items",
        indexes = {@Index(name="idx_items_uid", columnList = "uid")}
)
public class Item extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private List<Image> images;

    private String itemName;
    private BigInteger sellingPrice;
    private BigInteger initialPrice;

}
