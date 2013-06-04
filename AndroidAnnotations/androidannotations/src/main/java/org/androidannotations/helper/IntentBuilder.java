package org.androidannotations.helper;

import com.sun.codemodel.*;
import org.androidannotations.holder.HasIntentBuilder;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.*;

public class IntentBuilder {

	protected HasIntentBuilder holder;
	protected JFieldVar contextField;
	protected JClass contextClass;
	protected JClass intentClass;

	public IntentBuilder(HasIntentBuilder holder) {
		this.holder = holder;
		contextClass = holder.classes().CONTEXT;
		intentClass = holder.classes().INTENT;
	}

	public void build() throws JClassAlreadyExistsException {
		createClass();
		createConstructor();
		createGet();
		createFlags();
		createIntent();
	}

	private void createClass() throws JClassAlreadyExistsException {
		holder.setIntentBuilderClass(holder.getGeneratedClass()._class(PUBLIC | STATIC, "IntentBuilder_"));
		contextField = holder.getIntentBuilderClass().field(PRIVATE, contextClass, "context_");
		holder.setIntentField(holder.getIntentBuilderClass().field(PRIVATE | FINAL, intentClass, "intent_"));
	}

	private void createConstructor() {
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorContextParam = constructor.param(contextClass, "context");
		JBlock constructorBody = constructor.body();
		constructorBody.assign(contextField, constructorContextParam);
		constructorBody.assign(holder.getIntentField(), _new(intentClass).arg(constructorContextParam).arg(holder.getGeneratedClass().dotclass()));
	}

	private void createGet() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, intentClass, "get");
		method.body()._return(holder.getIntentField());
	}

	private void createFlags() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), "flags");
		JVar flagsParam = method.param(holder.codeModel().INT, "flags");
		JBlock body = method.body();
		body.invoke(holder.getIntentField(), "setFlags").arg(flagsParam);
		body._return(_this());
	}

	private void createIntent() {
		JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
		JVar contextParam = method.param(contextClass, "context");
		method.body()._return(_new(holder.getIntentBuilderClass()).arg(contextParam));
	}
}
