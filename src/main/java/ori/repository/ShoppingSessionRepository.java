package ori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ori.entity.ShoppingSession;
import ori.entity.ShoppingSessionKey;

@Repository
public interface ShoppingSessionRepository extends JpaRepository<ShoppingSession, ShoppingSessionKey>{

	@Query(value = "SELECT * FROM shopping_session where  userId = :id ORDER BY proid DESC limit 4", nativeQuery = true)
	List<ShoppingSession> findByUser(@Param("id") Integer userId);
}
