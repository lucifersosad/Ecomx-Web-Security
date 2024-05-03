package ori.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data

@AllArgsConstructor

@NoArgsConstructor
public class UserModel {
	private Integer userId;
	private String userName;
	private String password;
	private String fullName;
	private String email;
	private String phone;
	private String address;
	private Boolean active;
	private Boolean isEdit=false;
	private String code;
	private Boolean isEnabled;
}
