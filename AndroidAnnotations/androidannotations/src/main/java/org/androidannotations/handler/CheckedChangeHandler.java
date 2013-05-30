package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class CheckedChangeHandler extends AbstractListenerHandler {

	public CheckedChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(CheckedChange.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.param.hasZeroOrOneCompoundButtonOrTwoCompoundButtonBooleanParameters(executableElement, valid);

		return valid.isValid();
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar btnParam = listenerMethod.param(classes().COMPOUND_BUTTON, "buttonView");
		JVar isCheckedParam = listenerMethod.param(codeModel().BOOLEAN, "isChecked");
		boolean isCheckedParamExists = parameters.size() == 2;
		boolean btnParamExists = parameters.size() >= 1;

		if (btnParamExists) {
			call.arg(btnParam);
		}
		if (isCheckedParamExists) {
			call.arg(isCheckedParam);
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().VOID, "onCheckedChanged");
	}

	@Override
	protected String getSetterName() {
		return "setOnCheckedChangeListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER;
	}

	@Override
	protected JType getViewClass() {
		return classes().COMPOUND_BUTTON;
	}
}
