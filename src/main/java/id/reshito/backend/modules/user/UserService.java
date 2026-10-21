package id.reshito.backend.modules.user;

import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.common.exception.BadRequestException;
import id.reshito.backend.common.util.PhoneNumberNormalizer;
import id.reshito.backend.modules.customer.dto.CustomerResponse;
import id.reshito.backend.modules.user.dto.UserEditRequest;
import id.reshito.backend.modules.user.dto.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOrCreate(UserRequest request) {
        String phoneNumber = normalizeAndValidatePhone(request.phoneNumber());
        return userRepository.findByUid(request.uid())
                .orElseGet(() -> {
                    var user = User.builder()
                            .uid(request.uid())
                            .email(request.email())
                            .phoneNumber(phoneNumber)
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

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<User> findAllByPhoneNumber(String phoneNumber) {
        return userRepository.findAllByPhoneNumber(PhoneNumberNormalizer.normalize(phoneNumber));
    }

    public Optional<User> findByPhoneNUmber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> findAllByNameIgnoreCase(String name) {
        return userRepository.findAllByNameIgnoreCase(name);
    }

    public CustomerResponse toCustomerResponse(User user) {
        return new CustomerResponse(user.getId(), user.getName(), user.getPhoneNumber());
    }

    public String normalizePhoneNumber(String phoneNumber) {
        return normalizeAndValidatePhone(phoneNumber);
    }

    public String normalizePhoneSearchQuery(String query) {
        String normalized = PhoneNumberNormalizer.normalize(query);
        return normalized == null ? "" : normalized.replaceAll("[^0-9]", "");
    }

    public User createManualBuyer(String name, String phoneNumber) {
        String normalizedPhone = normalizeAndValidatePhone(phoneNumber);
        return userRepository.save(User.builder()
                .uid("manual:" + UUID.randomUUID())
                .name(name)
                .phoneNumber(normalizedPhone)
                .roles(new HashSet<>(List.of(RoleEnum.BUYER)))
                .build());
    }

    public User update(String uid, @Valid UserEditRequest request) {
        var user = userRepository.findByUid(uid).orElseThrow(NotFoundException::new);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhoneNumber(normalizeAndValidatePhone(request.phoneNumber()));
        userRepository.save(user);
        return user;
    }

    private static String normalizeAndValidatePhone(String phoneNumber) {
        String normalized = PhoneNumberNormalizer.normalize(phoneNumber);
        if (normalized != null && !normalized.isBlank() && !normalized.matches("[1-9][0-9]{7,15}")) {
            throw new BadRequestException(
                    "Phone number must contain 8 to 16 digits including country code, without a plus sign");
        }
        return normalized;
    }
}
