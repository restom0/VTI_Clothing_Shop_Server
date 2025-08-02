package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vti.clothing_shop.entities.Audit;

public interface LogRepository extends JpaRepository<Audit, Long> {
}
