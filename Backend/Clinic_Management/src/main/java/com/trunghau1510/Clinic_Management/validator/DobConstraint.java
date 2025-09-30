package com.trunghau1510.Clinic_Management.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {

    String message() default "INVALID_DATE";
    int min();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}