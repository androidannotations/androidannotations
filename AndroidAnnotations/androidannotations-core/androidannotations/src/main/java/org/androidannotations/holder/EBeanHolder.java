/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EBeanHolder extends EComponentWithViewSupportHolder {

	public static final String GET_INSTANCE_METHOD_NAME = "getInstance" + generationSuffix();
	public static final String SET_INSTANCE_MOCK_METHOD_NAME = "setMock" + generationSuffix();

	private JFieldVar contextField;
	private JMethod constructor;

	public EBeanHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		setConstructor();
	}

	private void setConstructor() {
		constructor = generatedClass.constructor(PRIVATE);
		JVar constructorContextParam = constructor.param(getClasses().CONTEXT, "context");
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
			contextField = generatedClass.field(PRIVATE, getClasses().CONTEXT, "context" + generationSuffix());
		}
		return contextField;
	}

	@Override
	protected void setContextRef() {
		contextRef = getContextField();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
	}

	public void invokeInitInConstructor() {
		JBlock constructorBody = constructor.body();
		constructorBody.invoke(getInit());
	}

	public void createFactoryMethod(boolean hasSingletonScope) {

		AbstractJClass narrowedGeneratedClass = codeModelHelper.narrowGeneratedClass(generatedClass, annotatedElement.asType());
		
		JFieldVar mockInstance = generatedClass.field(PRIVATE | STATIC, generatedClass, "mockInstance" + generationSuffix());
		JMethod setMockMethod = generatedClass.method(PUBLIC | STATIC, getCodeModel().VOID, SET_INSTANCE_MOCK_METHOD_NAME);
		JBlock setMockBody = setMockMethod.body();
		JVar param = setMockMethod.param(generatedClass, "mock");
		setMockBody.assign(mockInstance, param);

		JMethod factoryMethod = generatedClass.method(PUBLIC | STATIC, narrowedGeneratedClass, GET_INSTANCE_METHOD_NAME);

		codeModelHelper.generify(factoryMethod, annotatedElement);

		JVar factoryMethodContextParam = factoryMethod.param(getClasses().CONTEXT, "context");

		JBlock rawMethodBody = factoryMethod.body();
		// we use the default body only in case of that the mock is null
		JBlock factoryMethodBody = rawMethodBody._if(mockInstance.eq(_null()))._then();
		// otherwise we just return the mock
		rawMethodBody._return(mockInstance);
		/*
		 * Singletons are bound to the application context
		 */
		if (hasSingletonScope) {

			JFieldVar instanceField = generatedClass.field(PRIVATE | STATIC, generatedClass, "instance" + generationSuffix());

			JBlock creationBlock = factoryMethodBody //
					._if(instanceField.eq(_null())) //
					._then();
			JVar previousNotifier = viewNotifierHelper.replacePreviousNotifierWithNull(creationBlock);
			creationBlock.assign(instanceField, _new(narrowedGeneratedClass).arg(factoryMethodContextParam.invoke("getApplicationContext")));
			creationBlock.invoke(instanceField, getInit());
			viewNotifierHelper.resetPreviousNotifier(creationBlock, previousNotifier);

			factoryMethodBody._return(instanceField);
		} else {
			factoryMethodBody._return(_new(narrowedGeneratedClass).arg(factoryMethodContextParam));
		}
	}

	public void createRebindMethod() {
		JMethod rebindMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "rebind");
		JVar contextParam = rebindMethod.param(getClasses().CONTEXT, "context");
		JBlock body = rebindMethod.body();
		body.assign(getContextField(), contextParam);
		body.invoke(getInit());
	}
}
