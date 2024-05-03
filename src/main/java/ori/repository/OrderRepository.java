package ori.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ori.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ori.model.OrderModel;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{
	@Query(value = "SELECT IFNULL(sum(CASE WHEN currency=\"VND\" THEN total\r\n"
			+ "					WHEN currency = \"USD\" THEN total *24390.243902 END ), 0) as total_month FROM `orders`  \r\n"
			+ "WHERE MONTH(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = MONTH(CURRENT_DATE()) \r\n"
			+ "AND status=1 ;", nativeQuery = true)
    int revenueOnCurrentMonth();
	@Query(value = "SELECT IFNULL(sum(CASE WHEN currency=\"VND\" THEN total\r\n"
			+ "					WHEN currency = \"USD\" THEN total *24390.243902 END ), 0) as total_month FROM `orders`  \r\n"
			+ "WHERE YEAR(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = YEAR(CURRENT_DATE()) \r\n"
			+ "AND status=1 ;", nativeQuery = true)
    int revenueOnCurrentYear();
	@Query(value = "SELECT IFNULL(sum(status)/count(*),0)*100 as completed_rate\r\n"
			+ "FROM `orders` \r\n", nativeQuery = true)
    int rateCompleted();
	@Query(value = "SELECT IFNULL(sum(CASE WHEN currency=\"VND\" THEN total\r\n"
			+ "					WHEN currency = \"USD\" THEN total *24390.243902 END ), 0) as total_month FROM `orders`  \r\n"
			+ "WHERE QUARTER(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = QUARTER(CURRENT_DATE()) \r\n"
			+ "AND status=1 ;", nativeQuery = true)
    int revenueOnCurrentQuarter();
	@Query("""
		    SELECT o FROM Order o WHERE o.userId.userId = :userId
		""")
		List<Order> findOrderByUserId(@Param("userId") Integer userId);
	
	@Query(nativeQuery = true, value =
	        "WITH AllMonths AS (\r\n"
	        + "  SELECT 1 AS month_number UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 \r\n"
	        + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 \r\n"
	        + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12\r\n"
	        + ")\r\n"
	        + "SELECT  IFNULL(SUM(CASE WHEN o.currency = \"VND\" THEN o.total\r\n"
	        + "                                        WHEN o.currency = \"USD\" THEN o.total * 23000\r\n"
	        + "                                   END), 0) AS total_sum, m.month_number\r\n"
	        + "FROM AllMonths m\r\n"
	        + "LEFT JOIN wck.orders o ON MONTH(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = m.month_number\r\n"
	        + "                      AND o.status = 1\r\n"
	        + "GROUP BY m.month_number;"
	    )
	    List<Integer> getMonthlyTotal();
	
	@Query(nativeQuery = true, value =
	        "WITH AllQuarters AS (\r\n"
	        + "  SELECT 1 AS quarter_number UNION SELECT 2 UNION SELECT 3 UNION SELECT 4\r\n"
	        + ")\r\n"
	        + "SELECT  IFNULL(SUM(CASE WHEN o.currency = \"VND\" THEN o.total\r\n"
	        + "                                      WHEN o.currency = \"USD\" THEN o.total * 24390.243902\r\n"
	        + "                                 END), 0) AS total_sum\r\n"
	        + "FROM AllQuarters q\r\n"
	        + "LEFT JOIN wck.orders o ON QUARTER(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = q.quarter_number\r\n"
	        + "                    AND o.status = 1\r\n"
	        + "GROUP BY q.quarter_number;"
	    )
	    List<Integer> getQuarterTotal();
	
	@Query(nativeQuery = true, value = "SELECT * FROM ORDERS ORDER BY date DESC")
    Page<Order> findAllCustom(Pageable pageable);
}