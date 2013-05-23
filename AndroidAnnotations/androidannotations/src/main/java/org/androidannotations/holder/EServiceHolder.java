package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.ServiceIntentBuilder;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class EServiceHolder extends EComponentHolder implements HasIntentBuilder {

	private JDefinedClass intentBuilderClass;
	private JFieldVar intentField;

	public EServiceHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		createIntentBuilder();
	}

	private void createIntentBuilder() throws JClassAlreadyExistsException {
		new ServiceIntentBuilder(this).build();
	}

	@Override
	protected void setContextRef() {
		contextRef = _this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		createOnCreate();
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JBlock onCreateBody = onCreate.body();
		onCreateBody.invoke(getInit());
		onCreateBody.invoke(JExpr._super(), onCreate);
	}

	@Override
	public void setIntentBuilderClass(JDefinedClass intentBuilderClass) {
		this.intentBuilderClass = intentBuilderClass;
	}

	@Override
	public JDefinedClass getIntentBuilderClass() {
		return intentBuilderClass;
	}

	@Override
	public void setIntentField(JFieldVar intentField) {
		this.intentField = intentField;
	}

	@Override
	public JFieldVar getIntentField() {
		return intentField;
	}
}
