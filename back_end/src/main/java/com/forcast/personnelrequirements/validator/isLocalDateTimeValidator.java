package com.forcast.personnelrequirements.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class isLocalDateTimeValidator implements ConstraintValidator<isLocalDateTime, LocalDateTime> {


    private LocalDateTime minDate;
    private LocalDateTime maxDate;
    boolean minUseCurrentDate = false;
    boolean maxUseCurrentDate = false;
    private boolean parseFail = false;

    @Override
    public void initialize(isLocalDateTime constraintAnnotation) {
        minUseCurrentDate = constraintAnnotation.minUseCurrentDate();
        maxUseCurrentDate = constraintAnnotation.maxUseCurrentDate();
        try {
            if (!constraintAnnotation.min().isEmpty()) {
                minDate = LocalDateTime.parse(constraintAnnotation.min(), DateTimeFormatter.ofPattern(constraintAnnotation.pattern()));
            }
            if (!constraintAnnotation.max().isEmpty()) {
                maxDate = LocalDateTime.parse(constraintAnnotation.max(), DateTimeFormatter.ofPattern(constraintAnnotation.pattern()));
            }
        } catch (Exception e) {
            parseFail = true;
        }
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (minUseCurrentDate) {
            minDate = LocalDateTime.now();
        }
        if (maxUseCurrentDate) {
            maxDate = LocalDateTime.now();
        }
        if (parseFail) {
            return false;
        }
        // 如果value为null，返回true，因为这里只校验日期是否在范围内，不校验是否为null
        if (value == null) {
            return true;
        }
        // 如果minDate和maxDate都为null，不进行校验，返回true
        if (minDate == null && maxDate == null) {
            return true;
        }
        // 如果minDate不为null，且value小于minDate，返回false
        if (minDate != null && value.isBefore(minDate)) {
            return false;
        }
        // 如果maxDate不为null，且value大于maxDate，返回false
        return maxDate == null || !value.isAfter(maxDate);
    }
}
