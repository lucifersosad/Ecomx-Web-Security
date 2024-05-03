package ori.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor

@NoArgsConstructor
public class CartModel {
	private Integer proid;
	private Integer userid;
	private Integer quantity;
	private Boolean isEdit=false;
	
}
