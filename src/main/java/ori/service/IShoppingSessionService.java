package ori.service;

import java.util.List;

import ori.entity.ShoppingSession;

public interface IShoppingSessionService {
	
	List<ShoppingSession> findByUser(Integer userId);
	List<ShoppingSession> findAll();
	
	<S extends ShoppingSession> S save(S entity);
}
