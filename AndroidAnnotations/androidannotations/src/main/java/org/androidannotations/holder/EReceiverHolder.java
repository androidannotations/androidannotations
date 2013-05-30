package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class EReceiverHolder extends EComponentHolder {

	private JFieldVar contextField;

	public EReceiverHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		contextField = generatedClass.field(PRIVATE, classes().CONTEXT, "context_");
		contextRef = contextField;
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		createOnReceive();
	}

	private void createOnReceive() {
		JMethod onReceive = generatedClass.method(PUBLIC, codeModel().VOID, "onReceive");
		JVar contextParam = onReceive.param(classes().CONTEXT, "context");
		JVar intentParam = onReceive.param(classes().INTENT, "intent");
		onReceive.annotate(Override.class);
		JBlock onReceiveBody = onReceive.body();
		onReceiveBody.assign(getContextField(), contextParam);
		onReceiveBody.invoke(getInit());
		onReceiveBody.invoke(JExpr._super(), onReceive).arg(contextParam).arg(intentParam);
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			setContextRef();
		}
		return contextField;
	}
}