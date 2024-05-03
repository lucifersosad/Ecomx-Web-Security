package ori.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ori.common.enums.UserRole;

@Entity
@Table(name="[role]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="role_name")
    @Enumerated(EnumType.STRING)
    private UserRole role;

}
