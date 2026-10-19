package id.reshito.backend.modules.shipping.repository;

import id.reshito.backend.modules.shipping.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    java.util.List<Shipping> findAllByOrderByIdAsc();
}
