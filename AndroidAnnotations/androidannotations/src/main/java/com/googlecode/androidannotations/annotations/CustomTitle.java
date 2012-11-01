package com.googlecode.androidannotations.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used on Activity classes that must have custom title layout.
 *
 * The activity must be annotated with {@link EActivity}.
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface CustomTitle {
    int value() default ResId.DEFAULT_VALUE;
}
