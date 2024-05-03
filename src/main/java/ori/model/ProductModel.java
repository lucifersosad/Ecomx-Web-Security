package ori.model;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

@Data

@AllArgsConstructor

@NoArgsConstructor

public class ProductModel {
	private Integer proId;
	private String name;
	private String description;
	private int stock;
	private int sale;
	private float price;
	private float oldPrice;
	private String image_link;
	private Integer cateId;
	private Integer brandId;
	private MultipartFile imageFile;
	private Boolean isEdit = false;
	
}