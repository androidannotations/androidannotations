package org.androidannotations.annotations.sharedpreferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.androidannotations.annotations.ResId;

/**
 * Use on methods in {@link SharedPref} annotated class. The generated method
 * will return an empty {@link java.util.Set} of Strings by default.
 * <p/>
 * The key of the preference will be the method name by default. This can be
 * overridden by specifying a string resource with the {@link #keyRes()}
 * parameter.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface DefaultStringSet {

	int keyRes() default ResId.DEFAULT_VALUE;
}
