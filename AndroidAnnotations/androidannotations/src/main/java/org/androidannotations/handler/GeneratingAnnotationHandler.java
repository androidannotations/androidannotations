package org.androidannotations.handler;

import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

public interface GeneratingAnnotationHandler<T extends GeneratedClassHolder> extends AnnotationHandler<T> {

	T createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception;
}
