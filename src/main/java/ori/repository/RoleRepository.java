package ori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ori.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
}
