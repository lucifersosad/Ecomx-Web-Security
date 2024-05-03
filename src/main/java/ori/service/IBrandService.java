package ori.service;

import java.util.List;
import java.util.Optional;
import ori.entity.Brand;

public interface IBrandService {

	void deleteById(Integer id);

	long count();

	Optional<Brand> findById(Integer id);

	List<Brand> findAllById(Iterable<Integer> ids);

	List<Brand> findAll();

	<S extends Brand> S save(S entity);

}
