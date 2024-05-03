package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ori.entity.Category;
import ori.entity.Order;
import ori.entity.Product;
public interface IOrderService {

	void deleteById(Integer id);

	long count();

	Optional<Order> findById(Integer id);

	List<Order> findAll();

	Page<Order> findAll(Pageable pageable);

	<S extends Order> S save(S entity);
	 int reOnCurrentMonth();
	 
	 int reOnCurrentYear();
	 
	 int reOnCurrentQuarter();
	 
	 float rateCom();
	 
	 List<Order> findOder(Integer userId);
	 
	 Page<Order> getAll(Integer pageNo);
	 
	 void updateOrderState(Integer orderId, int newState);
	 List<Integer> getMonthlyTotal();
	 List<Integer> getQuarterTotal();
}