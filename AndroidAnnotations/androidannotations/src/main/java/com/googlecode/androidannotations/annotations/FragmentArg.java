package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use on fields in fragments. This String value field corresponds to the
 * argument name.
 * 
 * When {@link FragmentArg} is used on fields in a Fragment, the fragment
 * builder will hold dedicated methods for these arguments.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface FragmentArg {

	String value() default "";
}
