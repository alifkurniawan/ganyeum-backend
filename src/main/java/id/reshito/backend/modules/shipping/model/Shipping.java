package id.reshito.backend.modules.shipping.model;

import id.reshito.backend.common.util.BaseModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_methods")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Shipping extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipping_methods_seq")
    @SequenceGenerator(name = "shipping_methods_seq", sequenceName = "shipping_methods_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "requires_tracking", nullable = false)
    private boolean requiresTracking;
}
