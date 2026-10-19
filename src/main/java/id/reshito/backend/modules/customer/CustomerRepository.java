package id.reshito.backend.modules.customer;

import id.reshito.backend.modules.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findBySellerId(UUID sellerId, Pageable pageable);
    boolean existsBySellerIdAndCustomerId(UUID sellerId, UUID customerId);
    Optional<Customer> findBySellerIdAndCustomerId(UUID sellerId, UUID customerId);

    @Query("""
            select distinct c.customer from Customer c
            where c.seller.id = :sellerId and c.customer.phoneNumber = :phone
            """)
    List<User> findBySellerAndPhone(@Param("sellerId") UUID sellerId, @Param("phone") String phone);

    @Query("""
            select distinct c.customer from Customer c
            where c.seller.id = :sellerId and lower(c.customer.name) = lower(:name)
            """)
    List<User> findBySellerAndExactName(@Param("sellerId") UUID sellerId, @Param("name") String name);

    @Query(value = """
            select c from Customer c
            where c.seller.id = :sellerId and (
                lower(c.customer.name) like lower(concat('%', :query, '%'))
                or (:phoneQuery <> '' and c.customer.phoneNumber like concat('%', :phoneQuery, '%'))
            )
            """,
            countQuery = """
            select count(distinct c.customer.id) from Customer c
            where c.seller.id = :sellerId and (
                lower(c.customer.name) like lower(concat('%', :query, '%'))
                or (:phoneQuery <> '' and c.customer.phoneNumber like concat('%', :phoneQuery, '%'))
            )
            """)
    Page<Customer> searchBySeller(
            @Param("sellerId") UUID sellerId,
            @Param("query") String query,
            @Param("phoneQuery") String phoneQuery,
            Pageable pageable
    );
}
