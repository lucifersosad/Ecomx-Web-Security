package ori.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="userid")
	private Integer userId;
	@Column(name="passwordHash")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String passwordHash;
	@Column(name="passwordSalt")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String passwordSalt;
	@Column(name="fullname")
	private String fullName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="phone")
	private String phone;
	
	@Column(name="address")
	private String address;
	@Column(name="isEnabled")
	private Boolean isEnabled;
	@Column(name="username")
	private String username;
	@Column(name="code")
	private String code;


	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
		name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Roles> roles = new HashSet<>();



}
