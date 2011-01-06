package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used on item click listener methods for AdapterView classes
 * 
 * The method may have 4 parameter
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ItemClick {
	public static final int DEFAULT_VALUE = -1;

	int value() default DEFAULT_VALUE;
}