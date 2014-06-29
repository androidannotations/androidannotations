package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ensures that method is called from the UI thread. If it is not, then
 * {@link java.lang.IllegalStateException} will be thrown (by default).
 * //TODO how to change default
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SupposeUiThread {
}
