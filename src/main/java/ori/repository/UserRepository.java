package ori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ori.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	@Query("""
		select u from User u where u.username = :name or u.email = :name
	""")
	Optional<User> findByUsernameOrEmail(@Param("name") String name);
}