package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class EProviderHolder extends EComponentHolder {

	public EProviderHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		contextRef = invoke("getContext");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		createOnCreate();
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().BOOLEAN, "onCreate");
		onCreate.annotate(Override.class);
		JBlock onCreateBody = onCreate.body();
		onCreateBody.invoke(getInit());
		onCreateBody._return(invoke(_super(), onCreate));
	}
}
