package com.leih.url.shop.annotation;

import java.lang.annotation.*;

/**
 * @author leih
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventRepeatSubmission {
    /**
     * Support two methods
     */
    enum Type {PARAM, TOKEN}

    /**
     * Default method for preventing submitting the form repeatedly
     * @return
     */
    Type limitType() default Type.PARAM;

    /**
     * Expiration time for a lock: 5 seconds
     * @return
     */
    long lockTime() default 5;
}
