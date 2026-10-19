package id.reshito.backend.modules.customer;

import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.modules.user.UserService;
import id.reshito.backend.modules.customer.dto.CreateCustomerRequest;
import id.reshito.backend.modules.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        var sellerId = userService.findByUid(jwt.getSubject()).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(sellerId, request));
    }

    @GetMapping
    public ListResponse search(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @NotBlank @Size(max = 100) String query,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return customerService.search(userService.findByUid(jwt.getSubject()).getId(), query.trim(), page, size);
    }
}
