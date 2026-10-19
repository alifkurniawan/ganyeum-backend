package id.reshito.backend.modules.user;

import id.reshito.backend.common.util.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_phone_number", columnList = "phoneNumber")
        }
)
public class User extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String phoneNumber;

    @Column(name = "uid", unique = true, nullable = false)
    private String uid;

    private String photoProfile;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_user_roles_user_role",
                            columnNames = {"user_id", "role"}
                    )
            }
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<RoleEnum> roles = new HashSet<>();
}
