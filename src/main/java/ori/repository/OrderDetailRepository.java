package ori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ori.entity.OrderDetail;
import ori.entity.OrderDetailKey;
import ori.entity.Product;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailKey>{
	@Query("""
		    SELECT u FROM Product u 
		    JOIN OrderDetail d ON u.proId = d.product.proId 
		    JOIN Order o ON d.order.orderId = o.orderId 
		    WHERE o.userId.userId = :userId
		""")
		List<Product> findProductByUserId(@Param("userId") Integer userId);
}
