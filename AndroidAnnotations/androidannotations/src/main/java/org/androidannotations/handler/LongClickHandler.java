package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class LongClickHandler extends AbstractListenerHandler {

	public LongClickHandler(ProcessingEnvironment processingEnvironment) {
		super(LongClick.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.zeroOrOneViewParameter(executableElement, valid);

		return valid.isValid();
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;
		if (returnMethodResult) {
			listenerMethodBody._return(call);
		} else {
			listenerMethodBody.add(call);
			listenerMethodBody._return(JExpr.TRUE);
		}
	}

	@Override
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		boolean hasViewParameter = parameters.size() == 1;
		JVar viewParam = listenerMethod.param(classes().VIEW, "view");
		if (hasViewParameter) {
			call.arg(viewParam);
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onLongClick");
	}

	@Override
	protected String getSetterName() {
		return "setOnLongClickListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().VIEW_ON_LONG_CLICK_LISTENER;
	}
}
