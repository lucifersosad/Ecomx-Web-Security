package ori.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "promotionid")
	private int promotionId;
	@Column(name = "name")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "discount_rate")
	private int discount_rate;
	@Column(name = "is_active")
	private int is_active;
}
