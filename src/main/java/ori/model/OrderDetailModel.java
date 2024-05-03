package ori.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor

@NoArgsConstructor
public class OrderDetailModel {
	private Integer orderId;
	private Integer proId;
	private int quantity;
	private float discount;
}
