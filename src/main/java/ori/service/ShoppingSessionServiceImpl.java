package ori.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ori.entity.ShoppingSession;
import ori.repository.ShoppingSessionRepository;

@Service
public class ShoppingSessionServiceImpl implements IShoppingSessionService{
	@Autowired
	ShoppingSessionRepository shoppingSessionRepository;
	
	public ShoppingSessionServiceImpl(ShoppingSessionRepository shoppingSessionRepository) {
		this.shoppingSessionRepository = shoppingSessionRepository;
	}
	@Override
	public List<ShoppingSession> findByUser(Integer userId) {
		return shoppingSessionRepository.findByUser(userId);
	}
	@Override
	public List<ShoppingSession> findAll() {
		return shoppingSessionRepository.findAll();
	}
	@Override
	public <S extends ShoppingSession> S save(S entity) {
		return shoppingSessionRepository.save(entity);
	}

}
