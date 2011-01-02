package com.googlecode.androidannotations.model;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;


public class ModelExtractor {
	
	public AnnotationElementsHolder extract(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		AnnotationElementsHolder extractedModel = new AnnotationElementsHolder();
		
		for (TypeElement annotation : annotations) {
			extractedModel.put(annotation, roundEnv.getElementsAnnotatedWith(annotation));
		}
		
		return extractedModel;
	}
}
