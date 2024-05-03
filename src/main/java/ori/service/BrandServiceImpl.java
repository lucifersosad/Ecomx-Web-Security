package ori.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ori.entity.Brand;
import ori.repository.BrandRepository;

@Service
public class BrandServiceImpl implements IBrandService {
	@Autowired
	BrandRepository brandRepository;

	public BrandServiceImpl(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	@Override
	public <S extends Brand> S save(S entity) {
		return brandRepository.save(entity);
	}

	@Override
	public List<Brand> findAll() {
		return brandRepository.findAll();
	}

	@Override
	public List<Brand> findAllById(Iterable<Integer> ids) {
		return brandRepository.findAllById(ids);
	}

	@Override
	public Optional<Brand> findById(Integer id) {
		return brandRepository.findById(id);
	}

	@Override
	public long count() {
		return brandRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		brandRepository.deleteById(id);
	}
	
}
