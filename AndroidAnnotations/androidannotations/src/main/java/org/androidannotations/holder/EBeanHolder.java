package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.helper.FindFragmentHelper;
import org.androidannotations.helper.ViewNotifierHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.List;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JMod.*;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

public class EBeanHolder extends EComponentHolder implements HasViewChanged {

	public static final String GET_INSTANCE_METHOD_NAME = "getInstance" + GENERATION_SUFFIX;

	private ViewNotifierHelper viewNotifierHelper;
	private ViewChangedHolder viewChangedHolder;
	private JFieldVar contextField;
	private JMethod constructor;
	private JMethod findNativeFragmentById;
	private JMethod findSupportFragmentById;

	public EBeanHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		viewNotifierHelper = new ViewNotifierHelper(this);
		setConstructor();
	}

	private void setConstructor() {
		constructor = generatedClass.constructor(PRIVATE);
		JVar constructorContextParam = constructor.param(classes().CONTEXT, "context");
		JBlock constructorBody = constructor.body();
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(annotatedElement.getEnclosedElements());
		ExecutableElement superConstructor = constructors.get(0);
		if (superConstructor.getParameters().size() == 1) {
			constructorBody.invoke("super").arg(constructorContextParam);
		}
		constructorBody.assign(getContextField(), constructorContextParam);
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			contextField = generatedClass.field(PRIVATE, classes().CONTEXT, "context_");
		}
		return contextField;
	}

	@Override
	protected void setContextRef() {
		contextRef = getContextField();
	}
	protected void setInit() {
		init = generatedClass.method(PRIVATE, processHolder.codeModel().VOID, "init_");
		JBlock constructorBody = constructor.body();
		constructorBody.invoke(init);
	}

	public void createFactoryMethod(boolean hasSingletonScope) {

		JMethod factoryMethod = generatedClass.method(PUBLIC | STATIC, generatedClass, GET_INSTANCE_METHOD_NAME);

		JVar factoryMethodContextParam = factoryMethod.param(classes().CONTEXT, "context");

		JBlock factoryMethodBody = factoryMethod.body();

			/*
			 * Singletons are bound to the application context
			 */
		if (hasSingletonScope) {

			JFieldVar instanceField = generatedClass.field(PRIVATE | STATIC, generatedClass, "instance_");

			JBlock creationBlock = factoryMethodBody //
					._if(instanceField.eq(_null())) //
					._then();
			JVar previousNotifier = viewNotifierHelper.replacePreviousNotifierWithNull(creationBlock);
			creationBlock.assign(instanceField, _new(generatedClass).arg(factoryMethodContextParam.invoke("getApplicationContext")));
			viewNotifierHelper.resetPreviousNotifier(creationBlock, previousNotifier);

			factoryMethodBody._return(instanceField);
		} else {
			factoryMethodBody._return(_new(generatedClass).arg(factoryMethodContextParam));
		}
	}

	public void createRebindMethod() {
		JMethod rebindMethod = generatedClass.method(PUBLIC, codeModel().VOID, "rebind");
		JVar contextParam = rebindMethod.param(classes().CONTEXT, "context");
		JBlock body = rebindMethod.body();
		body.assign(getContextField(), contextParam);
		body.invoke(getInit());
	}

	@Override
	public ViewChangedHolder getOnViewChangedHolder() {
		if (viewChangedHolder == null) {
			setViewChangedHolder();
		}
		return viewChangedHolder;
	}

	private void setViewChangedHolder() {
		viewChangedHolder = ViewChangedHolder.createViewChangedHolder(this);
	}

	@Override
	public JMethod getFindNativeFragmentById() {
		if (findNativeFragmentById == null) {
			setFindNativeFragmentById();
		}
		return findNativeFragmentById;
	}

	private void setFindNativeFragmentById() {
		findNativeFragmentById = FindFragmentHelper.createFindNativeFragmentById(this);
	}

	@Override
	public JMethod getFindSupportFragmentById() {
		if (findSupportFragmentById == null) {
			setFindSupportFragmentById();
		}
		return findSupportFragmentById;
	}

	private void setFindSupportFragmentById() {
		findSupportFragmentById = FindFragmentHelper.createFindSupportFragmentById(this);
	}
}
