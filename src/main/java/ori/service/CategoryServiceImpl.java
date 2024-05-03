package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ori.entity.Category;
import ori.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements ICategoryService {
	@Autowired
	CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public <S extends Category> S save(S entity) {
		return categoryRepository.save(entity);
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public List<Category> findAllById(Iterable<Integer> ids) {
		return categoryRepository.findAllById(ids);
	}

	@Override
	public Optional<Category> findById(Integer id) {
		return categoryRepository.findById(id);
	}

	@Override
	public long count() {
		return categoryRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		categoryRepository.deleteById(id);
	}
	
	@Override
	public List<Category> findTop10() {
		Pageable pageable = PageRequest.of(0, 10);
		return categoryRepository.findAll(pageable).toList();
	}

	@Override
	public Page<Category> getAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 10);
		return categoryRepository.findAll(pageable);
	}
	
}
