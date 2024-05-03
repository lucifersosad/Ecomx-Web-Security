package ori.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "shopping_session")
public class ShoppingSession {
	@EmbeddedId
    private ShoppingSessionKey id;
	
	@ManyToOne
    @MapsId("proId")
    @JoinColumn(name = "proid")
    private Product product;
	
	@ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;
	
	String date;

}
