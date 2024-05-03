package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ori.entity.Cart;
import ori.entity.CartKey;
import ori.repository.CartRepository;

@Service
public class CartServiceImpl implements ICartService{
	@Autowired
	CartRepository cartRepository;
	@Override
	public List<Cart> findByUserId(Integer userId) {

		return cartRepository.findByUserId(userId);
	}
	@Override
	public void delete(Cart entity) {
		cartRepository.delete(entity);
	}
	@Override
	public List<Cart> findByUserIdAndProid(Integer userId, Integer proId) {
		
		return cartRepository.findByUserIdAndProid(userId, proId);
	}
	@Override
	public <S extends Cart> S save(S entity) {
		return cartRepository.save(entity);
	}
	@Override
	public void addtocart(Integer proId, Integer userId, Integer quantity) {
		cartRepository.addtocart(proId, userId, quantity);
		
	}

}
