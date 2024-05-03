package ori.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpRequest {
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String username;
}
