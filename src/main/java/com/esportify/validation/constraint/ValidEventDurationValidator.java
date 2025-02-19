package com.esportify.validation.constraint;

import com.esportify.dto.EventRequest;
import com.esportify.validation.ValidEventDuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class ValidEventDurationValidator implements ConstraintValidator<ValidEventDuration, EventRequest> {

    private int minMinutes;

    @Override
    public void initialize(ValidEventDuration constraintAnnotation) {
        this.minMinutes = constraintAnnotation.minMinutes();
    }

    @Override
    public boolean isValid(EventRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getStartDateTime() == null || request.getEndDateTime() == null) {
            return false;
        }

        long duration = Duration.between(request.getStartDateTime(), request.getEndDateTime()).toMinutes();
        return duration >= minMinutes;
    }
}
