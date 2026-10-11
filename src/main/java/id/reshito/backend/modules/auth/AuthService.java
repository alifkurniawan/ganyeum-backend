package id.reshito.backend.modules.auth;

import com.google.api.gax.rpc.UnauthenticatedException;
import id.reshito.backend.common.exception.InvalidFirebaseTokenException;
import id.reshito.backend.modules.auth.FirebaseTokenService;
import id.reshito.backend.modules.auth.JwtTokenService;
import id.reshito.backend.modules.auth.dto.TokenResponse;
import id.reshito.backend.modules.user.RoleEnum;
import id.reshito.backend.modules.user.UserService;
import id.reshito.backend.modules.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FirebaseTokenService firebaseTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public TokenResponse exchangeToken(String idToken) throws InvalidFirebaseTokenException {
        if (idToken.isBlank()) {
            throw new IllegalStateException();
        }

        var claims = this.firebaseTokenService.verify(idToken);
        var user = userService.findOrCreate(new UserRequest(
                claims.getUid(),
                claims.getName(),
                claims.getEmail(),
                (String) claims.getClaims().get("phone"),
                claims.getPicture()));

        return this.jwtTokenService.createAccessToken(user.getUid(), user.getRoles().stream().toList());
    }



}
