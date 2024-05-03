package ori.entity;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CartKey {
	private Integer userid;
	private Integer proid;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartKey ratingKey = (CartKey) o;
        return Objects.equals(userid, ratingKey.userid) &&
               Objects.equals(proid, ratingKey.proid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, proid);
    }
}


