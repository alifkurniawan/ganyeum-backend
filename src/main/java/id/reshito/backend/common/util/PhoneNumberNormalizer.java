package id.reshito.backend.common.util;

public final class PhoneNumberNormalizer {
    private PhoneNumberNormalizer() {}

    public static String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return null;
        String normalized = phoneNumber.trim().replaceAll("[\\s().-]", "").replaceFirst("^\\+", "");
        return normalized.isBlank() ? null : normalized;
    }
}
