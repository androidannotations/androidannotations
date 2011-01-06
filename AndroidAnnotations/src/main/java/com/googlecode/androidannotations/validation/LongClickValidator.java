package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class LongClickValidator extends ValidatorHelper implements ElementValidator {

	private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
	private final RClass rClass;

	public LongClickValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return LongClick.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		warnVoidReturnType(element, executableElement);

		validateRFieldName(element, valid);

		validateParameters(element, valid, executableElement);

		validateIsNotPrivate(element, valid);

		return valid.isValid();
	}

	private void validateParameters(Element element, IsValid valid, ExecutableElement executableElement) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() != 0 && parameters.size() != 1) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a method with zero or one parameter, instead of " + parameters.size());
		}

		if (parameters.size() == 1) {
			VariableElement parameter = parameters.get(0);
			TypeMirror parameterType = parameter.asType();
			if (!parameterType.toString().equals(ANDROID_VIEW_QUALIFIED_NAME)) {
				valid.invalidate();
				printAnnotationError(element, annotationName()
						+ " should only be used on a method with no parameter or a parameter of type android.view.View, not " + parameterType);
			}
		}
	}

	private void validateRFieldName(Element element, IsValid valid) {
		LongClick annotation = element.getAnnotation(LongClick.class);
		int idValue = annotation.value();

		RInnerClass rInnerClass = rClass.get(Res.ID);
		if (idValue == Click.DEFAULT_VALUE) {
			String methodName = element.getSimpleName().toString();
			if (!rInnerClass.containsField(methodName)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R.id." + methodName);
			}
		} else {
			if (!rInnerClass.containsIdValue(idValue)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R.id." + idValue);
			}
		}
	}

	private void warnVoidReturnType(Element element, ExecutableElement executableElement) {
		TypeMirror returnType = executableElement.getReturnType();

		if (returnType.getKind() != TypeKind.BOOLEAN) {
			printAnnotationWarning(element, annotationName() + " should only be used on a method with a boolean return type ");
		}
	}
}
