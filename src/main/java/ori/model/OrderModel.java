package ori.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor

@NoArgsConstructor
public class OrderModel {
	private Integer orderId;

	private String order_date;

	private String payment_method;

	private String shipping_method;

	private int order_status;

	private Double order_total;

	private String order_currency;
	
	private Integer userId;
	
	private Boolean isEdit = false;
}
