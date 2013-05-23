package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.*;

public class EApplicationHolder extends EComponentHolder {

	public static final String GET_APPLICATION_INSTANCE = "getInstance";

	private JFieldVar staticInstanceField;

	public EApplicationHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		createSingleton();
		createOnCreate();
	}

	private void createSingleton() {
		JClass annotatedComponent = generatedClass._extends();

		staticInstanceField = generatedClass.field(PRIVATE | STATIC, annotatedComponent, "INSTANCE_");
        // Static singleton getter and setter
        JMethod getInstance = generatedClass.method(PUBLIC | STATIC, annotatedComponent, GET_APPLICATION_INSTANCE);
        getInstance.body()._return(staticInstanceField);

        JMethod setInstance = generatedClass.method(PUBLIC | STATIC, codeModel().VOID, "setForTesting");
        setInstance.javadoc().append("Visible for testing purposes");
        JVar applicationParam = setInstance.param(annotatedComponent, "application");
        setInstance.body().assign(staticInstanceField, applicationParam);
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JBlock onCreateBody = onCreate.body();
		onCreateBody.assign(staticInstanceField, _this());
		onCreateBody.invoke(getInit());
		onCreateBody.invoke(_super(), onCreate);
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr._this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
	}
}
