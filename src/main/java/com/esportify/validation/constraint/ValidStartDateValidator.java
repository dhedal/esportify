package com.esportify.validation.constraint;

import com.esportify.validation.ValidStartDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ValidStartDateValidator implements ConstraintValidator<ValidStartDate, LocalDateTime> {
    private int days;
    @Override
    public void initialize(ValidStartDate constraintAnnotation) {
        this.days = constraintAnnotation.days();
    }

    @Override
    public boolean isValid(LocalDateTime startDateTime, ConstraintValidatorContext context) {
        if (startDateTime == null) {
            return false; // La date de début ne doit pas être null
        }

        long daysBetween = ChronoUnit.DAYS.between(LocalDateTime.now(), startDateTime);
        // Vérifie si l'événement commence au moins 3 jours après aujourd'hui
        return daysBetween >= this.days;
    }
}
