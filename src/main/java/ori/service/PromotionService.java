package ori.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ori.entity.Promotion;
import ori.repository.PromotionRepository;

@Service
public class PromotionService implements IPromotionServive{
	@Autowired
	PromotionRepository promoRepository;

	public PromotionService(PromotionRepository promoRepository) {
		this.promoRepository = promoRepository;
	}

	@Override
	public Promotion findpromotionByname(String name) {
		return promoRepository.findPromotionByname(name);
	}

	@Override
	public <S extends Promotion> S save(S entity) {
		return promoRepository.save(entity);
	}

	@Override
	public List<Promotion> findAll() {
		return promoRepository.findAll();
	}

	@Override
	public void deleteById(Integer id) {
		promoRepository.deleteById(id);
	}

	@Override
	public long count() {
		return promoRepository.count();
	}
	
	
	

}
