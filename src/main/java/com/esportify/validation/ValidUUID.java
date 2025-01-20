package com.esportify.validation;

import com.esportify.validation.constraint.UUIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface ValidUUID {
    String message() default "Le format de l'UUID est invalide";
    Class<?> [] groups() default {};
    Class<? extends Payload>[] payload() default {};
}