package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.AnnotationElements;
import com.googlecode.androidannotations.Layout;
import com.googlecode.androidannotations.RClass;
import com.googlecode.androidannotations.RClass.Res;
import com.googlecode.androidannotations.RInnerClass;

public class LayoutValidator extends ElementValidatorHelper {

	private static final String ANDROID_ACTIVITY_QUALIFIED_NAME = "android.app.Activity";
	private final RClass rClass;
	private final TypeElement activityTypeElement;

	public LayoutValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		activityTypeElement = typeElementFromQualifiedName(ANDROID_ACTIVITY_QUALIFIED_NAME);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Layout.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		TypeElement typeElement = (TypeElement) element;
		if (isSubtype(typeElement, activityTypeElement)) {
			
			Layout layoutAnnotation = element.getAnnotation(Layout.class);
			int layoutIdValue = layoutAnnotation.value();
			
			RInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			
			if (rInnerClass.containsIdValue(layoutIdValue)) {
				return true;
			} else {
				printAnnotationError(element, "Layout id value not found in R.layout.*: "+layoutIdValue);
			}
		} else {
			printAnnotationError(element, Layout.class + " should only be used on Activity subclasses");
		}
		return false;
	}

}
