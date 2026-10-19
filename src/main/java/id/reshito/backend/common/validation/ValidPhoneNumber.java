package id.reshito.backend.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Phone number must contain 8 to 16 digits, including country code, without a plus sign";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
