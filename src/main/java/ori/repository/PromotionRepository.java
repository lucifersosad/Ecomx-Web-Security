package ori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ori.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer>{
	
	Promotion findPromotionByname(String name);
}
