package org.gfg.DigitalLibrary.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class StudentAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    int  minAge;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        this.minAge = constraintAnnotation.age();
    }

     @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        if (dob == null) {
            return true;
        }

        LocalDate today = LocalDate.now();

        // DOB cannot be in the future
        if (dob.isAfter(today)) {
            return false;
        }

        int age = Period.between(dob, today).getYears();

        return age >= minAge;
    }
}

