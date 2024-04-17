package com.forcast.personnelrequirements.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Target(value = {
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = isLocalDateTimeValidator.class
)

public @interface isLocalDateTime {
    String message() default "时间范围不正确";

    String pattern() default "yyyy-MM-dd HH:mm:ss";
    // 最小值
    String min() default "";
    // 最大值
    String max() default "";
    // 最小值取当前时间，即：参数需大于当前时间
    boolean minUseCurrentDate() default false;
    // 最大值取当前时间，即：参数需小于当前时间
    boolean maxUseCurrentDate() default false;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
