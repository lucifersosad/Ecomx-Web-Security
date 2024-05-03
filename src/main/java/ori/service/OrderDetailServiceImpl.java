package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ori.entity.OrderDetail;
import ori.entity.OrderDetailKey;
import ori.entity.Product;
import ori.repository.OrderDetailRepository;

@Service
public class OrderDetailServiceImpl implements IOrderDetailService{
	@Autowired
	OrderDetailRepository orderDetailRepository;

	
	public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
		this.orderDetailRepository = orderDetailRepository;
	}

	@Override
	public <S extends OrderDetail> S save(S entity) {
		return orderDetailRepository.save(entity);
	}

	@Override
	public List<OrderDetail> findAll() {
		return orderDetailRepository.findAll();
	}

	@Override
	public Optional<OrderDetail> findById(OrderDetailKey id) {
		return orderDetailRepository.findById(id);
	}

	@Override
	public long count() {
		return orderDetailRepository.count();
	}

	@Override
	public void deleteById(OrderDetailKey id) {
		orderDetailRepository.deleteById(id);
	}
	
	@Override
	public List<Product> listProByOderID(Integer userId){
		return orderDetailRepository.findProductByUserId(userId);
	}
	
}
