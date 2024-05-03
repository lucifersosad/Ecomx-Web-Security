package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import ori.entity.Cart;
import ori.entity.CartKey;

public interface ICartService {

	
	List<Cart> findByUserId( Integer userId);

	void delete(Cart entity);


	List<Cart> findByUserIdAndProid(Integer userId,Integer proId);

	<S extends Cart> S save(S entity);

	void addtocart(Integer proId, Integer userId, Integer quantity);




	
	

	


	


}
