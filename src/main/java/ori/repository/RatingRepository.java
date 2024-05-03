package ori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ori.entity.Product;
import ori.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
	@Query(value = "SELECT * FROM rating where display = 1 order by ratingId desc", nativeQuery = true)
	List<Rating> findAllRatingDisplayed();
}
