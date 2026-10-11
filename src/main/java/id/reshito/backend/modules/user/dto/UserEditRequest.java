package id.reshito.backend.modules.user.dto;

public record UserEditRequest(
        String name,
        String email,
        String phoneNumber
) {
}
