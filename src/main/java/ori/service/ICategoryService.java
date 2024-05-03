package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ori.entity.Category;
import ori.entity.Product;

public interface ICategoryService {

	void deleteById(Integer id);

	long count();

	Optional<Category> findById(Integer id);

	List<Category> findAllById(Iterable<Integer> ids);

	List<Category> findAll();

	<S extends Category> S save(S entity);

	List<Category> findTop10();
	
	Page<Category> getAll(Integer pageNo);
}
