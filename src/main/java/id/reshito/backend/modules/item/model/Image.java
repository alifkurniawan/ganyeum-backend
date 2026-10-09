package id.reshito.backend.modules.item.model;

import id.reshito.backend.common.util.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "item_images")
public class Image extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private String mimeType;
    private int fileSize;
}
