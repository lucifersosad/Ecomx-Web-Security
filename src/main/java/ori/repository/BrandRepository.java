package ori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ori.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
