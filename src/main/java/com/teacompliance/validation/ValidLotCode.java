package com.teacompliance.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 茶葉ロットコードのバリデーションアノテーション
 */
@Documented
@Constraint(validatedBy = LotCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLotCode {
    
    String message() default "ロットコードの形式が正しくありません。形式: TL-YYYY-NNN";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
