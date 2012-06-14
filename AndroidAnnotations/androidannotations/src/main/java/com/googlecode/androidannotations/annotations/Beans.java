package com.googlecode.androidannotations.annotations;

public @interface Beans {
	Class<?>[] value() default Void.class;
}
