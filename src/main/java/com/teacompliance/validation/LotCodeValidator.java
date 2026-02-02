package com.teacompliance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ロットコードバリデーションの実装クラス
 */
public class LotCodeValidator implements ConstraintValidator<ValidLotCode, String> {
    
    private static final Pattern LOT_CODE_PATTERN = Pattern.compile("^TL-\\d{4}-\\d{3}$");
    
    @Override
    public void initialize(ValidLotCode constraintAnnotation) {
        // 初期化処理（必要な場合）
    }
    
    @Override
    public boolean isValid(String lotCode, ConstraintValidatorContext context) {
        if (lotCode == null) {
            return false;
        }
        return LOT_CODE_PATTERN.matcher(lotCode).matches();
    }
}
