package ori.model;

import jakarta.persistence.Column;
import lombok.*;

@Data

@AllArgsConstructor

@NoArgsConstructor

public class RatingModel {

	private Integer ratingId;

	private String nickname;
	
	private String content;
	
	private String platform;
	
	private Integer rate;
	
	private String date;
	
	private int display;

}