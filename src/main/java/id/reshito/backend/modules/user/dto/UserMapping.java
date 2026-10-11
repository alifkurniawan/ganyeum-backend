package id.reshito.backend.modules.user.dto;

import id.reshito.backend.modules.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapping {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPhotoProfile(),
                user.getRoles()
        );
    }
}
