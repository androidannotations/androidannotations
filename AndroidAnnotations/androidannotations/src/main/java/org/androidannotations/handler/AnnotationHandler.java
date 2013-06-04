package org.androidannotations.handler;

import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

import javax.lang.model.element.Element;

public interface AnnotationHandler<T extends GeneratedClassHolder> {

	String getTarget();
	boolean validate(Element element, AnnotationElements validatedElements);
	void process(Element element, T holder) throws Exception;

	void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest);
	void setValidatedModel(AnnotationElements validatedModel);
}
