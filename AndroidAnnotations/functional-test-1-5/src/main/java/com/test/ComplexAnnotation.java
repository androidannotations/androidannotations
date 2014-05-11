package com.test;

public @interface ComplexAnnotation {
	SimpleAnnotation value();
	SimpleAnnotation[] array() default {};
}

