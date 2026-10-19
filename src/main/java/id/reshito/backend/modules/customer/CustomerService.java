package id.reshito.backend.modules.customer;

import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.modules.user.User;
import id.reshito.backend.modules.user.UserService;
import id.reshito.backend.modules.customer.dto.CreateCustomerRequest;
import id.reshito.backend.modules.customer.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Transactional
    public CustomerResponse create(UUID sellerId, CreateCustomerRequest request) {
        User seller = userService.findById(sellerId);
        User customer = resolveBuyer(seller, null, request.name(), request.phoneNumber());
        return userService.toCustomerResponse(customer);
    }

    public ListResponse search(UUID sellerId, String query, int page, int size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        if (query == null || query.isBlank()) {
            var result = customerRepository.findBySellerId(sellerId, pageable)
                    .map(link -> userService.toCustomerResponse(link.getCustomer()));
            return new ListResponse(result.getContent(), page, result.getTotalPages(), result.getTotalElements());
        }

        query = query.trim();
        String phoneQuery = userService.normalizePhoneSearchQuery(query);
        var result = customerRepository.searchBySeller(
                sellerId, query, phoneQuery,
                pageable
        ).map(link -> userService.toCustomerResponse(link.getCustomer()));
        return new ListResponse(result.getContent(), page, result.getTotalPages(), result.getTotalElements());
    }

    @Transactional
    public User resolveBuyer(User seller, UUID customerId, String name, String phoneNumber) {
        if (customerId != null) {
            User customer = userService.findById(customerId);
            if (!customerRepository.existsBySellerIdAndCustomerId(seller.getId(), customer.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer does not belong to this seller");
            }
            return customer;
        }

        String phone = userService.normalizePhoneNumber(phoneNumber);
        List<User> byPhone = userService.findAllByPhoneNumber(phone);
        if (byPhone.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ambiguous customer phone number");
        }

        User customer;
        if (!byPhone.isEmpty()) {
            customer = byPhone.getFirst();
        } else {
            List<User> byName = userService.findAllByNameIgnoreCase(name.trim());
            if (byName.size() > 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ambiguous customer name");
            }
            if (!byName.isEmpty()) {
                customer = byName.getFirst();
                if (!Objects.equals(userService.normalizePhoneNumber(customer.getPhoneNumber()), phone)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer name exists with a different phone number");
                }
            } else {
                customer = userService.createManualBuyer(name.trim(), phone);
            }
        }
        if (!customerRepository.existsBySellerIdAndCustomerId(seller.getId(), customer.getId())) {
            customerRepository.save(Customer.builder().seller(seller).customer(customer).build());
        }
        return customer;
    }
}
