package id.reshito.backend.modules.auth.dto;

import com.google.firebase.database.annotations.NotNull;

public record TokenExchangeRequest(
        String idToken
) {
}
