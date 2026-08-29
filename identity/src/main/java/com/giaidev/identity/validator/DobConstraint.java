package com.giaidev.identity.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({FIELD, PARAMETER})
@Retention(RUNTIME) // Xử lý lúc nào
@Constraint(validatedBy = DobValidator.class) // Phải có DobValidator.class mới sử dụng được
public @interface DobConstraint { // Để custom annotation thì phải có Validator để valid thông tin cần valid
    String message() default "INVALID_DOB";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}