package ori.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShoppingSessionKey implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "userid")
    Integer userId;

    @Column(name = "proid")
    Integer proId;

    // standard constructors, getters, and setters
    // hashcode and equals implementation
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingSessionKey that = (ShoppingSessionKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(proId, that.proId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, proId);
    }
}