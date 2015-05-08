/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EBeanHolder extends EComponentWithViewSupportHolder {

	public static final String GET_INSTANCE_METHOD_NAME = "getInstance" + generationSuffix();

	private JFieldVar contextField;
	private JMethod constructor;

	public EBeanHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
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
			contextField = generatedClass.field(PRIVATE, classes().CONTEXT, "context" + generationSuffix());
		}
		return contextField;
	}

	@Override
	protected void setContextRef() {
		contextRef = getContextField();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, processHolder.codeModel().VOID, "init" + generationSuffix());
	}

	public void invokeInitInConstructor() {
		JBlock constructorBody = constructor.body();
		constructorBody.invoke(getInit());
	}

	public void createFactoryMethod(boolean hasSingletonScope) {

		JClass narrowedGeneratedClass = codeModelHelper.narrowGeneratedClass(generatedClass, annotatedElement.asType(), this);

		JMethod factoryMethod = generatedClass.method(PUBLIC | STATIC, narrowedGeneratedClass, GET_INSTANCE_METHOD_NAME);

		codeModelHelper.generifyStaticHelper(this, factoryMethod, annotatedElement);

		JVar factoryMethodContextParam = factoryMethod.param(classes().CONTEXT, "context");

		JBlock factoryMethodBody = factoryMethod.body();

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
		JMethod rebindMethod = generatedClass.method(PUBLIC, codeModel().VOID, "rebind");
		JVar contextParam = rebindMethod.param(classes().CONTEXT, "context");
		JBlock body = rebindMethod.body();
		body.assign(getContextField(), contextParam);
		body.invoke(getInit());
	}
}
