package id.reshito.backend.modules.customer.dto;

import id.reshito.backend.common.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @ValidPhoneNumber String phoneNumber
) {}
