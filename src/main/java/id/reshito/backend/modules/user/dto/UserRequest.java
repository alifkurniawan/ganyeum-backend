package id.reshito.backend.modules.user.dto;

import id.reshito.backend.common.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Size;

public record UserRequest (
        String uid,
        String name,
        String email,
        @Size(max = 12) @ValidPhoneNumber String phoneNumber,
        String photoProfile
) {
}
