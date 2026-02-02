package com.teacompliance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 農薬レベルバリデーションの実装クラス
 */
public class PesticideLevelValidator implements ConstraintValidator<ValidPesticideLevel, Double> {
    
    private double maxValue;
    
    @Override
    public void initialize(ValidPesticideLevel constraintAnnotation) {
        this.maxValue = constraintAnnotation.max();
    }
    
    @Override
    public boolean isValid(Double pesticideLevel, ConstraintValidatorContext context) {
        if (pesticideLevel == null) {
            return false;
        }
        return pesticideLevel >= 0.0 && pesticideLevel <= maxValue;
    }
}
