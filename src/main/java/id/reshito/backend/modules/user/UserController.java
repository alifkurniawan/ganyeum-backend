package id.reshito.backend.modules.user;

import id.reshito.backend.modules.user.dto.UserEditRequest;
import id.reshito.backend.modules.user.dto.UserMapping;
import id.reshito.backend.modules.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "User", path = "/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final UserMapping userMapping;

    @PostMapping("/roles/seller")
    public ResponseEntity<UserResponse> activateSeller(@AuthenticationPrincipal Jwt jwt) {
        String uid = jwt.getSubject();
        User user = userService.addSeller(uid);
        return ResponseEntity.ok(userMapping.toResponse(user));
    }

    @PostMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        String uid = jwt.getSubject();
        User user = userService.findByUid(uid);
        return ResponseEntity.ok(userMapping.toResponse(user));
    }

    @PutMapping("/me/edit")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserEditRequest request
    ) {
        String uid = jwt.getSubject();
        User user = userService.update(uid, request);
        return ResponseEntity.ok(userMapping.toResponse(user));
    }

}
