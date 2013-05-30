package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class FocusChangeHandler extends AbstractListenerHandler {

	public FocusChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(FocusChange.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.param.hasZeroOrOneViewOrTwoViewBooleanParameters(executableElement, valid);

		return valid.isValid();
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar viewParam = listenerMethod.param(classes().VIEW, "view");
		JVar hasFocusParam = listenerMethod.param(codeModel().BOOLEAN, "hasFocus");
		boolean hasFocusParamExists = parameters.size() == 2;
		boolean viewParamExists = parameters.size() >= 1;

		if (viewParamExists) {
			call.arg(viewParam);
		}
		if (hasFocusParamExists) {
			call.arg(hasFocusParam);
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().VOID, "onFocusChange");
	}

	@Override
	protected String getSetterName() {
		return "setOnFocusChangeListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().VIEW_ON_FOCUS_CHANGE_LISTENER;
	}
}
