package ori.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	@Column(name = "date")
	private String date;
	@Column(name = "payment_method")
	private String payment_method;
	@Column(name = "shipping_method")
	private String shipping_method;
	@Column(name = "status")
	private int status;
	@Column(name = "total")
	private Double total;
	@Column(name = "currency")
	private String currency;
	@ManyToOne
	@JoinColumn(name="userid")
	private User userId;
}
