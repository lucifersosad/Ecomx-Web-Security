package ori.entity;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OrderDetail")
public class OrderDetail implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private OrderDetailKey id;
	@ManyToOne
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "proId", insertable = false, updatable = false)
    private Product product;
    
    private int quantity;
    private float discount;
}
