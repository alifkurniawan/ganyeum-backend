package id.reshito.backend.modules.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn

) {
}
