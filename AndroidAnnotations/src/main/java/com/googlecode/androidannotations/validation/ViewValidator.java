package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.AnnotationElements;
import com.googlecode.androidannotations.Layout;
import com.googlecode.androidannotations.RClass;
import com.googlecode.androidannotations.RClass.Res;
import com.googlecode.androidannotations.RInnerClass;
import com.googlecode.androidannotations.View;

public class ViewValidator extends ElementValidatorHelper {

	private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
	private final RClass rClass;
	private final TypeMirror viewTypeMirror;

	public ViewValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		viewTypeMirror = typeElementFromQualifiedName(ANDROID_VIEW_QUALIFIED_NAME).asType();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return View.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		Element enclosingElement = element.getEnclosingElement();

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (layoutAnnotatedElements.contains(enclosingElement)) {

			TypeMirror uiFieldTypeMirror = element.asType();

			if (uiFieldTypeMirror instanceof DeclaredType) {
				if (isSubtype(uiFieldTypeMirror, viewTypeMirror)) {

					View viewAnnotation = element.getAnnotation(View.class);
					int viewIdValue = viewAnnotation.value();

					RInnerClass rInnerClass = rClass.get(Res.ID);

					if (viewIdValue == View.DEFAULT_VALUE) {
						String fieldName = element.getSimpleName().toString();
						if (rInnerClass.containsField(fieldName)) {
							return true;
						} else {
							printAnnotationError(element, "Field name not found in R.id.* : " + fieldName);
						}
					} else {
						if (rInnerClass.containsIdValue(viewIdValue)) {
							return true;
						} else {
							printAnnotationError(element, "View id value not found in R.id.*: " + viewIdValue);
						}
					}
				} else {
					printAnnotationError(element, "@" + View.class.getSimpleName() + " should only be used on a field which type extends android.view.View");
				}
			} else {
				printAnnotationError(element, "@" + View.class.getSimpleName() + " should only be used on a field which is a declared type");
			}
		} else {
			printAnnotationError(element,
					"@" + View.class.getSimpleName() + " should only be used on a field in a class annotated with @" + Layout.class.getSimpleName());
		}
		return false;
	}

}
