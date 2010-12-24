package com.googlecode.androidannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface View {
	public static final int DEFAULT_VALUE =-1;
	
    int value() default DEFAULT_VALUE;
}