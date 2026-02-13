package org.gfg.DigitalLibrary.annotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Constraint(validatedBy = StudentAgeValidator.class)
public @interface ValidAge {

    String message() default "Age should not be less than 8";
    int age() default 8;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
