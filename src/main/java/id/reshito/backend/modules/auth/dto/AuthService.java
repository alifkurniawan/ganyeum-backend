package id.reshito.backend.modules.auth.dto;

import id.reshito.backend.common.exception.InvalidFirebaseTokenException;
import id.reshito.backend.modules.auth.FirebaseTokenService;
import id.reshito.backend.modules.auth.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FirebaseTokenService firebaseTokenService;
    private final JwtTokenService jwtTokenService;

    public TokenResponse exchangeToken(String idToken) throws InvalidFirebaseTokenException {
        if (idToken.isBlank()) {
            throw new IllegalStateException();
        }

        var claims = this.firebaseTokenService.verify(idToken);
        return this.jwtTokenService.createAccessToken(claims.getUid());
    }
}
