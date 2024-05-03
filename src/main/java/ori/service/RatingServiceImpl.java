package ori.service;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ori.entity.Product;
import ori.entity.Rating;
import ori.repository.RatingRepository;

@Service
public class RatingServiceImpl implements IRatingService{
	@Autowired
	RatingRepository ratingRepository;
	
	public RatingServiceImpl(RatingRepository ratingRepository) {
		this.ratingRepository = ratingRepository;
	}

	@Override
	public List<Rating> findAll() {
		return ratingRepository.findAll();
	}

	@Override
	public List<Rating> findAllById(Iterable<Integer> ids) {
		return ratingRepository.findAllById(ids);
	}

	@Override
	public Optional<Rating> findById(Integer id) {
		return ratingRepository.findById(id);
	}

	@Override
	public long count() {
		return ratingRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		ratingRepository.deleteById(id);
	}
	
	@Override
	public List<Rating> findAllRatingDisplayed() {
		return ratingRepository.findAllRatingDisplayed();
	}
	
	@Override
	public <S extends Rating> S save(S entity) {
		return ratingRepository.save(entity);
	}
	
	@Override
	public void updateRatingState(Integer ratingId, int newState) {
	 	Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        if (optionalRating.isPresent()) {
            Rating rating = optionalRating.get();
            rating.setDisplay(newState);
            ratingRepository.save(rating);
        }
	}
}

