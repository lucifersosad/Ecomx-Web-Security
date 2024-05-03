package ori.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OrderDetailKey implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer orderId;
	private Integer proId;
	
	@Override
	public int hashCode() {
		return Objects.hash(orderId, proId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderDetailKey other = (OrderDetailKey) obj;
		return Objects.equals(orderId, other.orderId) && Objects.equals(proId, other.proId);
	}
}
