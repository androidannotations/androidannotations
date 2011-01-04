package com.googlecode.androidannotations.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.ColorValue;
import com.googlecode.androidannotations.annotations.StringArrayValue;
import com.googlecode.androidannotations.annotations.StringResValue;
import com.googlecode.androidannotations.rclass.RClass.Res;

public enum AndroidValue {

	STRING(Res.STRING, StringResValue.class, "getString", "java.lang.String"), //
	STRING_ARRAY(Res.ARRAY, StringArrayValue.class, "getStringArray", "java.lang.String[]"), //
	COLOR(Res.COLOR, ColorValue.class, "getColor", "int", "java.lang.Integer");

	public static final int DEFAULT_VALUE = -1;

	private final Class<? extends Annotation> target;
	private final String resourceMethodName;
	private final List<String> allowedTypes;
	private final Res rInnerClass;

	AndroidValue(Res rInnerClass, Class<? extends Annotation> target, String resourceMethodName, String... allowedTypes) {
		this.target = target;
		this.resourceMethodName = resourceMethodName;
		this.allowedTypes = Arrays.asList(allowedTypes);
		this.rInnerClass = rInnerClass;
	}

	public Res getRInnerClass() {
		return rInnerClass;
	}

	public Class<? extends Annotation> getTarget() {
		return target;
	}

	public String getResourceMethodName() {
		return resourceMethodName;
	}

	public List<String> getAllowedTypes() {
		return allowedTypes;
	}

	public int idFromElement(Element element) {
		Annotation annotation = element.getAnnotation(target);
		Method valueMethod = target.getMethods()[0];
		try {
			return (Integer) valueMethod.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
