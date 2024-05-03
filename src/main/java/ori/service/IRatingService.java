package ori.service;

import java.util.List;
import java.util.Optional;

import ori.entity.Rating;

public interface IRatingService {

	void deleteById(Integer id);

	long count();

	Optional<Rating> findById(Integer id);

	List<Rating> findAllById(Iterable<Integer> ids);

	List<Rating> findAll();

	List<Rating> findAllRatingDisplayed();
	
	void updateRatingState(Integer ratingId, int newState);

	<S extends Rating> S save(S entity); 

}
