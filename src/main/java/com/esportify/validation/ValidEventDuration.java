package com.esportify.validation;

import com.esportify.validation.constraint.ValidEventDurationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEventDurationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventDuration {
    String message() default "La durée de l'événement doit être d'au moins {minMinutes} minutes";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minMinutes() default 30; // Défaut : 30 minutes
}
