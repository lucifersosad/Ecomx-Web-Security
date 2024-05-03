package ori.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private CartKey id;
	@Column(name="quantity")
	private int quantity;
	@OneToOne 
	@JoinColumn(name = "userid",referencedColumnName = "userid",insertable=false, updatable=false) 
	private User user;
	@ManyToOne
	@JoinColumn(name = "proid",insertable = false, updatable = false) 
	private Product product;
	public Cart(CartKey id, int quantity) {

		this.id = id;
		this.quantity = quantity;
	}
	public CartKey getId() {
		return id;
	}
	public void setId(CartKey id) {
		this.id = id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
}
