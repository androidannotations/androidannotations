package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.MetaModel;

public class ModelProcessor {

	private final List<ElementProcessor> processors = new ArrayList<ElementProcessor>();

	public void register(ElementProcessor processor) {
		processors.add(processor);
	}

	public MetaModel process(AnnotationElements validatedModel) {

		MetaModel metaModel = new MetaModel();

		for (ElementProcessor processor : processors) {
			Class<? extends Annotation> target = processor.getTarget();

			Set<? extends Element> annotatedElements = validatedModel.getAnnotatedElements(target);

			for (Element annotatedElement : annotatedElements) {
				processor.process(annotatedElement, metaModel);
			}
		}

		return metaModel;
	}

}
