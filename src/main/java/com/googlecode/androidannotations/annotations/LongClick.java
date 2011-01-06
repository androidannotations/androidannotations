
package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used on long click listener methods in activity classes
 * 
 * The method may have zero or one parameter, that MUST be of type
 * android.view.View .
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LongClick {
	public static final int DEFAULT_VALUE = -1;

	int value() default DEFAULT_VALUE;
}