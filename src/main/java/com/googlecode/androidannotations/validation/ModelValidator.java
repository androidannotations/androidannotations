package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.AnnotationElements;
import com.googlecode.androidannotations.AnnotationElementsHolder;

public class ModelValidator {
	
	private List<ElementValidator> validators = new ArrayList<ElementValidator>();
	
	public void register(ElementValidator validator) {
		validators.add(validator);
	}
	
	public AnnotationElements validate(AnnotationElementsHolder extractedModel) {
		
		AnnotationElementsHolder validatedElements = new AnnotationElementsHolder();
		
		for (ElementValidator validator : validators) {
			Class<? extends Annotation> target = validator.getTarget();
			
			Set<? extends Element> annotatedElements = extractedModel.getAnnotatedElements(target);
			
			TypeElement annotationElement = extractedModel.annotationElementfromAnnotationClass(target);
			Set<Element> validatedAnnotatedElements = new HashSet<Element>();
			
			validatedElements.put(annotationElement, validatedAnnotatedElements);
			
			for(Element annotatedElement : annotatedElements) {
				if (validator.validate(annotatedElement, validatedElements)) {
					validatedAnnotatedElements.add(annotatedElement);
				}
			}
		}
		return validatedElements;
	}
	

}
