package id.reshito.backend.modules.user.dto;

public record UserRequest (
        String uid,
        String name,
        String email,
        String phoneNumber,
        String photoProfile
) {
}
