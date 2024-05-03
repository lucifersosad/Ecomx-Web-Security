package ori.model;

import lombok.*;

@Data

@AllArgsConstructor

@NoArgsConstructor
public class ShoppingSessionModel {
	private Integer userId;
	private Integer proId;
	private Boolean isEdit = false;
}
