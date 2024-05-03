package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ori.entity.Order;
import ori.model.OrderModel;
import ori.repository.OrderRepository;

@Service
public class OrderServiceImpl implements IOrderService{
	@Autowired
	OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public <S extends Order> S save(S entity) {
		return orderRepository.save(entity);
	}

	@Override
	public Page<Order> findAll(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public long count() {
		return orderRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		orderRepository.deleteById(id);
	}
	public int reOnCurrentMonth() {

		return orderRepository.revenueOnCurrentMonth();
	}

	@Override
	public int reOnCurrentYear() {
		// TODO Auto-generated method stub
		return orderRepository.revenueOnCurrentYear();
	}

	@Override
	public float rateCom() {
		// TODO Auto-generated method stub
		return orderRepository.rateCompleted();
	}

	@Override
	public int reOnCurrentQuarter() {
		// TODO Auto-generated method stub
		return orderRepository.revenueOnCurrentQuarter();

	}

	@Override
	public List<Order> findOder(Integer userId) {
        return orderRepository.findOrderByUserId(userId);
    }

	@Override
	public Page<Order> getAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 10);
		return orderRepository.findAllCustom(pageable);
	}

	@Override
	public void updateOrderState(Integer orderId, int newState) {
		 Optional<Order> optionalOrder = orderRepository.findById(orderId);

	        if (optionalOrder.isPresent()) {
	            Order order = optionalOrder.get();
	            order.setStatus(newState);
	            orderRepository.save(order);
	        } else {
	}
}

	@Override
	public List<Integer> getMonthlyTotal() {
		// TODO Auto-generated method stub
		return orderRepository.getMonthlyTotal();
	}

	@Override
	public List<Integer> getQuarterTotal() {
		// TODO Auto-generated method stub
		return orderRepository.getQuarterTotal();
	}
}