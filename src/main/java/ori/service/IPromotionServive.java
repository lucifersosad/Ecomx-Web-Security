package ori.service;

import java.util.List;

import ori.entity.Promotion;

public interface IPromotionServive {

	void deleteById(Integer id);

	List<Promotion> findAll();

	<S extends Promotion> S save(S entity);

	Promotion findpromotionByname(String name);

	long count();

}
