package ori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ori.entity.Category;
import ori.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query(value = "SELECT *" + 
			"FROM ( SELECT *, ROW_NUMBER() OVER (PARTITION BY cateId ORDER BY sale DESC) AS RowNum FROM product) AS RankedProducts\r\n" +
			"WHERE RowNum = 1\r\n" + 
			"ORDER BY sale DESC\r\n" +
			"LIMIT 8", nativeQuery = true)
    List<Product> findProductsMostSaleByCategory();
	@Query(value = "SELECT * FROM product where brandId = :id ORDER BY ABS(price - :proPrice) LIMIT 4 ", nativeQuery = true)
	List<Product> findByBrand(@Param("id") Integer branId, @Param("proPrice") float proPrice);
	
	@Query(value = "SELECT * FROM product where cateId = :id ORDER BY ABS(price - :proPrice) LIMIT 4 ", nativeQuery = true)
	List<Product> findByCategory(@Param("id") Integer cateId, @Param("proPrice") float proPrice);
	
	Page<Product> findByCategory(Category category, Pageable pageable);
	List<Product> findByCategory(Category category);
	@Query("SELECT p FROM Product p WHERE p.price >= :startPrice AND p.price <= :endPrice")
    List<Product> findProductsByPriceRange(@Param("startPrice") float startPrice, @Param("endPrice") float endPrice);
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and (p.price*(100-p.sale))/100 <= :max_price")
    List<Product> findSearchProductsByPriceRange(@Param("keyword") String keyword,@Param("min_price") String min_price, @Param("max_price") String max_price);
	@Query(value = "SELECT *\r\n"
			+ "FROM product p\r\n"
			+ "JOIN (\r\n"
			+ "  SELECT pro_Id, SUM(quantity) AS total_quantity\r\n"
			+ "  FROM order_detail\r\n"
			+ "  GROUP BY pro_id\r\n"
			+ "  ORDER BY total_quantity DESC\r\n"
			+ "  limit 8\r\n"
			+ ") od ON p.proid = od.pro_id;", nativeQuery = true)
	List<Product> findProductsMostOrder();
	Optional<Product> findByName(String name);
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and (p.price*(100-p.sale))/100 <= :max_price")
	Page<Product> searchAllProductByName(@Param("keyword") String keyword, @Param("min_price") String min_price, @Param("max_price") String max_price,Pageable pageable);
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    Page<Product> searchProductByName(@Param("keyword") String keyword, Pageable pageable);
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> searchProductByName(@Param("keyword") String keyword);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY p.stock")
	Page<Product> sortSearchByStock(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY p.proId DESC")
	Page<Product> sortSearchByDate(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY (p.price*(100-p.sale))/100")
	Page<Product> sortSearchByPrice(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY (p.price*(100-p.sale))/100 DESC")
	Page<Product> sortSearchByPriceDesc(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and p.price <= :max_price ORDER BY p.stock")
	Page<Product> sortSearchByStockRangePrice(@Param("keyword") String keyword, @Param("min_price") String min_price, @Param("max_price") String max_price, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and p.price <= :max_price ORDER BY p.proId DESC")
	Page<Product> sortSearchByDateRangePrice(@Param("keyword") String keyword, @Param("min_price") String min_price, @Param("max_price") String max_price,Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and p.price <= :max_price ORDER BY (p.price*(100-p.sale))/100")
	Page<Product> sortSearchByPriceRangePrice(@Param("keyword") String keyword, @Param("min_price") String min_price, @Param("max_price") String max_price,Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% and (p.price*(100-p.sale))/100 >= :min_price and p.price <= :max_price ORDER BY (p.price*(100-p.sale))/100 DESC")
	Page<Product> sortSearchByPriceDescRangePrice(@Param("keyword") String keyword, @Param("min_price") String min_price, @Param("max_price") String max_price,Pageable pageable);
	
	
	
}