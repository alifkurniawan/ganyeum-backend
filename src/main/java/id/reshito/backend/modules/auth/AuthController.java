package id.reshito.backend.modules.auth;

import id.reshito.backend.common.exception.InvalidFirebaseTokenException;
import id.reshito.backend.modules.auth.dto.TokenExchangeRequest;
import id.reshito.backend.modules.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/exchange")
    public ResponseEntity<TokenResponse> exchangeToken(@RequestBody TokenExchangeRequest request) {
        TokenResponse response;
        try {
            response = authService.exchangeToken(request.idToken());
        } catch (InvalidFirebaseTokenException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }
}
