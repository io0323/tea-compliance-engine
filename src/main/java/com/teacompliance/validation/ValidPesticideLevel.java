package com.teacompliance.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 農薬レベルのバリデーションアノテーション
 */
@Documented
@Constraint(validatedBy = PesticideLevelValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPesticideLevel {
    
    String message() default "農薬レベルは0.0以上10.0以下である必要があります";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    double max() default 10.0;
}
