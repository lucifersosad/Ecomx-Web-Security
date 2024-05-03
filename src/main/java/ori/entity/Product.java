package ori.entity;

import java.io.Serializable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proid")
	private Integer proId;
	
	private String name;
	
	private String description;
	
	private int stock;
	
	private int sale;
	
	private float price;
	
	private String image_link;
	
	@ManyToOne
	@JoinColumn(name="cateid")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name="brandid")
	private Brand brand;

}
