package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ori.entity.Category;
import ori.entity.Product;
import ori.entity.User;
import ori.repository.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService  {
	@Autowired
	ProductRepository productRepository;

	@Override
	public <S extends Product> S save(S entity) {
		return productRepository.save(entity);
	}

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> findAllById(Iterable<Integer> ids) {
		return productRepository.findAllById(ids);
	}

	@Override
	public Optional<Product> findById(Integer id) {
		return productRepository.findById(id);
	}

	@Override
	public long count() {
		return productRepository.count();
	}

	@Override
	public void delete(Product entity) {
		productRepository.delete(entity);
	}
	
	@Override
	public List<Product> findTop10() {
		Pageable pageable = PageRequest.of(0, 10);
		return productRepository.findAll(pageable).toList();
	}
	public void deleteById(Integer id) {
		productRepository.deleteById(id);
		
	}

	@Override
	public List<Product> findProductsMostSaleByCategory() {
		return productRepository.findProductsMostSaleByCategory();
	}

	@Override
	public List<Product> findByBrand(Integer brandId, float proPrice) {
		return productRepository.findByBrand(brandId, proPrice);
	}

	@Override
	public List<Product> findByCategory(Integer cateId, float proPrice) {
		return productRepository.findByCategory(cateId, proPrice);
	}
	
	@Override
	public Page<Product> getAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 10);
		return productRepository.findAll(pageable);
	}
	@Override
	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}
	@Override
	public Page<Product> findByCategory(Category category, Pageable pageable) {
		return productRepository.findByCategory(category, pageable);
	}
	@Override
	public List<Product> findByCategory(Category category) {
		return productRepository.findByCategory(category);
	}
	@Override
	public List<Product> findProductsByPriceRange(float start_price, float end_price) {
		return productRepository.findProductsByPriceRange(start_price, end_price);
	}

	@Override
	public List<Product> findProductsMostOrder() {
		return productRepository.findProductsMostOrder();
	}
	@Override
	public Optional<Product> findByName(String name) {
		return productRepository.findByName(name);
	}
	@Override
	public Page<Product> searchProductByName(String keyword, Pageable pageable) {
		return productRepository.searchProductByName(keyword, pageable);
	}
	@Override
	public List<Product> searchProductByName(String keyword) {
		return productRepository.searchProductByName(keyword);
	}
	@Override
	public Page<Product> sortSearchByStock(String keyword, Pageable pageable) {
		return productRepository.sortSearchByStock(keyword, pageable);
	}
	@Override
	public Page<Product> sortSearchByDate(String keyword, Pageable pageable) {
		return productRepository.sortSearchByDate(keyword, pageable);
	}
	@Override
	public Page<Product> sortSearchByPrice(String keyword, Pageable pageable) {
		return productRepository.sortSearchByPrice(keyword, pageable);
	}
	@Override
	public Page<Product> sortSearchByPriceDesc(String keyword, Pageable pageable) {
		return productRepository.sortSearchByPriceDesc(keyword, pageable);
	}
	@Override
	public Page<Product> sortSearchByStockRangePrice(String keyword, String min_price, String max_price, Pageable pageable) {
		return productRepository.sortSearchByStockRangePrice(keyword, min_price, max_price, pageable);
	}
	@Override
	public Page<Product> searchAllProductByName(String keyword, String min_price, String max_price, Pageable pageable) {
		return productRepository.searchAllProductByName(keyword, min_price, max_price, pageable);
	}
	@Override
	public Page<Product> sortSearchByDateRangePrice(String keyword, String min_price, String max_price,
			Pageable pageable) {
		return productRepository.sortSearchByDateRangePrice(keyword, min_price, max_price, pageable);
	}
	@Override
	public Page<Product> sortSearchByPriceRangePrice(String keyword, String min_price, String max_price,
			Pageable pageable) {
		return productRepository.sortSearchByPriceRangePrice(keyword, min_price, max_price, pageable);
	}
	@Override
	public Page<Product> sortSearchByPriceDescRangePrice(String keyword, String min_price, String max_price,
			Pageable pageable) {
		return productRepository.sortSearchByPriceDescRangePrice(keyword, min_price, max_price, pageable);
	}
	@Override
	public List<Product> findSearchProductsByPriceRange(String keyword, String startPrice, String endPrice) {
		return productRepository.findSearchProductsByPriceRange(keyword, startPrice, endPrice);
	}
	
	
	
}