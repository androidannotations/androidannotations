package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class TargetAnnotationHelper extends AnnotationHelper implements HasTarget{
	
	private Class<? extends Annotation> target;
	
	public TargetAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target) {
		super(processingEnv);
		this.target = target;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T extractAnnotationValue(Element element) {
		Annotation annotation = element.getAnnotation(target);

		Method method;
		try {
			method = annotation.getClass().getMethod("value");
			return (T) method.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return target;
	}
	
	public String actionName() {
		return target.getSimpleName()+"ed";
	}
	
	public static String annotationName(Class<? extends Annotation> annotationClass) {
		return "@"+annotationClass.getSimpleName();
	}
	
	public String annotationName() {
		return annotationName(target);
	}
	
	/**
	 * @param message if the string contains a %s, it will be replaced with the annotation name (ex: @Override)
	 */
	public void printAnnotationError(Element annotatedElement, String message) {
		printAnnotationError(annotatedElement, target, String.format(message, annotationName()));
	}

	/**
	 * @param message if the string contains a %s, it will be replaced with the annotation name (ex: @Override)
	 */
	public void printAnnotationWarning(Element annotatedElement, String message) {
		printAnnotationWarning(annotatedElement, target, String.format(message, annotationName()));
	}
}
