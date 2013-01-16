package org.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.FocusChange;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class FocusChangeProcessor extends AbstractListenerProcessor {

	public FocusChangeProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv, rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FocusChange.class;
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar viewParam = listenerMethod.param(classes.VIEW, "view");
		JVar hasFocusParam = listenerMethod.param(codeModel.BOOLEAN, "hasFocus");
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
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel.VOID, "onFocusChange");
	}

	@Override
	protected String getSetterName() {
		return "setOnFocusChangeListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes.VIEW_ON_FOCUS_CHANGE_LISTENER;
	}

}
