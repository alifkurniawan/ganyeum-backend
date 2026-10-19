package id.reshito.backend.common.validation;

import id.reshito.backend.common.util.PhoneNumberNormalizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        String normalized = PhoneNumberNormalizer.normalize(value);
        return normalized != null && normalized.matches("[1-9][0-9]{7,15}");
    }
}
