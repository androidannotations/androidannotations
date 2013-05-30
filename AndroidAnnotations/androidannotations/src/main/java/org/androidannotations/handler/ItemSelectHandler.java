package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;

public class ItemSelectHandler extends AbstractListenerHandler {

	private JMethod onNothingSelectedMethod;

	public ItemSelectHandler(ProcessingEnvironment processingEnvironment) {
		super(ItemSelect.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.param.hasOneOrTwoParametersAndFirstIsBoolean(executableElement, valid);

		return valid.isValid();
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(JMethod listenerMethod, JInvocation itemSelectedCall, List<? extends VariableElement> parameters) {
		JClass narrowAdapterViewClass = classes().ADAPTER_VIEW.narrow(codeModel().wildcard());
		JVar onItemClickParentParam = listenerMethod.param(narrowAdapterViewClass, "parent");
		listenerMethod.param(classes().VIEW, "view");
		JVar onItemClickPositionParam = listenerMethod.param(codeModel().INT, "position");
		listenerMethod.param(codeModel().LONG, "id");

		itemSelectedCall.arg(JExpr.TRUE);
		boolean hasItemParameter = parameters.size() == 2;
		boolean secondParameterIsInt = false;
		String secondParameterQualifiedName = null;
		if (hasItemParameter) {
			VariableElement secondParameter = parameters.get(1);
			TypeMirror secondParameterType = secondParameter.asType();
			secondParameterQualifiedName = secondParameterType.toString();
			secondParameterIsInt = secondParameterType.getKind() == TypeKind.INT;
		}

		if (hasItemParameter) {

			if (secondParameterIsInt) {
				itemSelectedCall.arg(onItemClickPositionParam);
			} else {
				itemSelectedCall.arg(JExpr.cast(refClass(secondParameterQualifiedName), invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
			}
		}

		onNothingSelectedMethod.param(narrowAdapterViewClass, "parent");
		JInvocation nothingSelectedCall = onNothingSelectedMethod.body().invoke(getMethodName());
		nothingSelectedCall.arg(JExpr.FALSE);
		if (hasItemParameter) {
			if (secondParameterIsInt) {
				nothingSelectedCall.arg(lit(-1));
			} else {
				nothingSelectedCall.arg(_null());
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		onNothingSelectedMethod = listenerAnonymousClass.method(JMod.PUBLIC, codeModel().VOID, "onNothingSelected");
		onNothingSelectedMethod.annotate(Override.class);
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().VOID, "onItemSelected");
	}

	@Override
	protected String getSetterName() {
		return "setOnItemSelectedListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().ON_ITEM_SELECTED_LISTENER;
	}

	@Override
	protected JType getViewClass() {
		return classes().ADAPTER_VIEW.narrow(codeModel().wildcard());
	}

}
