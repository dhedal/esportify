package com.esportify.validation;

import com.esportify.validation.constraint.ValidStartDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = ValidStartDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartDate {
    String message() default "L'événement doit être créé au moins {days} jours avant son début";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int days() default 3;
}
