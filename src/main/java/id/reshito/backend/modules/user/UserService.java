package id.reshito.backend.modules.user;

import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.modules.user.dto.UserEditRequest;
import id.reshito.backend.modules.user.dto.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOrCreate(UserRequest request) {
        return userRepository.findByUid(request.uid())
                .orElseGet(() -> {
                    var user = User.builder()
                            .uid(request.uid())
                            .email(request.email())
                            .phoneNumber(request.phoneNumber())
                            .name(request.name())
                            .roles(new HashSet<>(List.of(RoleEnum.BUYER)))
                            .build();
                    userRepository.save(user);
                    return user;
                });

    }

    public User addSeller(String uid) {
        return addRole(uid, RoleEnum.SELLER);
    }

    public User addBuyer(String uid) {
        return addRole(uid, RoleEnum.BUYER);
    }

    public User addAdmin(String uid) {
        return addRole(uid, RoleEnum.ADMIN);
    }

    public User removeSeller(String uid) {
        return removeRole(uid, RoleEnum.SELLER);
    }

    public User removeBuyer(String uid) {
        return removeRole(uid, RoleEnum.BUYER);
    }

    public User removeAdmin(String uid) {
        return removeRole(uid, RoleEnum.ADMIN);
    }

    public User addRole(String uid, RoleEnum role) {
        var user = userRepository.findByUid(uid).orElseThrow(NotFoundException::new);
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }

        return user;
    }

    public User removeRole(String uid, RoleEnum role) {
        var user = userRepository.findByUid(uid).orElseThrow(NotFoundException::new);
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }

        return user;
    }

    public User findByUid(String uid) {
        return userRepository.findByUid(uid).orElseThrow(NotFoundException::new);
    }

    public User update(String uid, @Valid UserEditRequest request) {
        var user = userRepository.findByUid(uid).orElseThrow(NotFoundException::new);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        userRepository.save(user);
        return user;
    }
}
