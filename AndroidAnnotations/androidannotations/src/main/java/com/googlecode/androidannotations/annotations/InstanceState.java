package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use it on fields in activities to save and restore their values when the
 * system calls onSaveInstanceState(Bundle) and onCreate(Bundle).
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface InstanceState {
}
