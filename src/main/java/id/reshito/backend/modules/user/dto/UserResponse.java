package id.reshito.backend.modules.user.dto;

import id.reshito.backend.modules.user.RoleEnum;

import java.util.Set;
import java.util.UUID;

public record UserResponse (
        UUID id,
        String name,
        String email,
        String phoneNumber,
        String photoProfile,
        Set<RoleEnum> roles
) {
}