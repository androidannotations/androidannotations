/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.bean.BeanHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EBeanHolder extends EComponentWithViewSupportHolder {

	public static final String GET_INSTANCE_METHOD_NAME = "getInstance" + generationSuffix();

	private JFieldVar rootFragmentField;
	private JFieldVar contextField;

	private JMethod constructor;
	private JMethod overloadedConstructor;

	public EBeanHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		setConstructors();
	}

	private void setConstructors() {
		constructor = generatedClass.constructor(PRIVATE);
		JVar constructorContextParam = constructor.param(getClasses().CONTEXT, "context");
		JBlock constructorBody = constructor.body();
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(annotatedElement.getEnclosedElements());
		ExecutableElement superConstructor = constructors.get(0);
		if (superConstructor.getParameters().size() == 1) {
			constructorBody.invoke("super").arg(constructorContextParam);
		}
		constructorBody.assign(getContextField(), constructorContextParam);

		overloadedConstructor = generatedClass.constructor(PRIVATE);
		JVar overloadedConstructorContextParam = overloadedConstructor.param(getClasses().CONTEXT, "context");
		JVar overloadedConstructorRootFragmentParam = overloadedConstructor.param(getClasses().OBJECT, "rootFragment");
		JBlock overloadedConstructorBody = overloadedConstructor.body();
		if (superConstructor.getParameters().size() == 1) {
			overloadedConstructorBody.invokeSuper().arg(constructorContextParam);
		}
		overloadedConstructorBody.assign(getContextField(), overloadedConstructorContextParam);
		overloadedConstructorBody.assign(getRootFragmentField(), overloadedConstructorRootFragmentParam);
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			contextField = generatedClass.field(PRIVATE, getClasses().CONTEXT, "context" + generationSuffix());
		}
		return contextField;
	}

	public JFieldVar getRootFragmentField() {
		if (rootFragmentField == null) {
			rootFragmentField = generatedClass.field(PRIVATE, getClasses().OBJECT, "rootFragment" + generationSuffix());
		}
		return rootFragmentField;
	}

	@Override
	protected void setContextRef() {
		contextRef = getContextField();
	}

	@Override
	protected void setRootFragmentRef() {
		rootFragmentRef = getRootFragmentField();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
	}

	public void invokeInitInConstructors() {
		JBlock constructorBody = constructor.body();
		constructorBody.invoke(getInit());

		JBlock overloadedConstructorBody = overloadedConstructor.body();
		overloadedConstructorBody.invoke(getInit());
	}

	public void createFactoryMethod(EBean.Scope scope) {

		AbstractJClass narrowedGeneratedClass = codeModelHelper.narrowGeneratedClass(generatedClass, annotatedElement.asType());

		JMethod factoryMethod = generatedClass.method(PUBLIC | STATIC, narrowedGeneratedClass, GET_INSTANCE_METHOD_NAME);
		codeModelHelper.generify(factoryMethod, annotatedElement);

		JVar factoryMethodContextParam = factoryMethod.param(getClasses().CONTEXT, "context");

		JBlock factoryMethodBody = factoryMethod.body();

		switch (scope) {
		case Default:
			factoryMethodBody._return(_new(narrowedGeneratedClass).arg(factoryMethodContextParam));
			createOverloadedFactoryMethod(scope);
			break;

		case Fragment:
			JVar beanVar = factoryMethodBody.decl(getGeneratedClass(), "bean", _new(narrowedGeneratedClass).arg(factoryMethodContextParam));
			factoryMethodBody.invoke(beanVar, getInit());
			factoryMethodBody._return(beanVar);

			createOverloadedFactoryMethod(scope);
			break;

		case Activity:
			requestInstanceFromBeanHolder(factoryMethodContextParam, factoryMethodContextParam, factoryMethodBody);

			beanVar = factoryMethodBody.decl(getGeneratedClass(), "bean", _new(narrowedGeneratedClass).arg(factoryMethodContextParam));
			factoryMethodBody.invoke(beanVar, getInit());
			factoryMethodBody._return(beanVar);
			break;

		case Singleton:
			JFieldVar instanceField = generatedClass.field(PRIVATE | STATIC, generatedClass, "instance" + generationSuffix());

			JBlock creationBlock = factoryMethodBody._if(instanceField.eq(_null()))._then();
			JVar previousNotifier = viewNotifierHelper.replacePreviousNotifierWithNull(creationBlock);
			creationBlock.assign(instanceField, _new(narrowedGeneratedClass).arg(factoryMethodContextParam.invoke("getApplicationContext")));
			creationBlock.invoke(instanceField, getInit());
			viewNotifierHelper.resetPreviousNotifier(creationBlock, previousNotifier);

			factoryMethodBody._return(instanceField);
			break;
		}
	}

	private void createOverloadedFactoryMethod(EBean.Scope scope) {
		AbstractJClass narrowedGeneratedClass = codeModelHelper.narrowGeneratedClass(generatedClass, annotatedElement.asType());
		JMethod factoryMethod = generatedClass.method(PUBLIC | STATIC, narrowedGeneratedClass, GET_INSTANCE_METHOD_NAME);
		codeModelHelper.generify(factoryMethod, annotatedElement);

		JVar factoryMethodContextParam = factoryMethod.param(getClasses().CONTEXT, "context");
		JVar factoryMethodRootFragmentParam = factoryMethod.param(getClasses().OBJECT, "rootFragment");

		JBlock factoryMethodBody = factoryMethod.body();

		if (scope == EBean.Scope.Fragment) {
			requestInstanceFromBeanHolder(factoryMethodRootFragmentParam, factoryMethodContextParam, factoryMethodBody);
		}

		factoryMethodBody._return(_new(narrowedGeneratedClass).arg(factoryMethodContextParam).arg(factoryMethodRootFragmentParam));
	}

	private void requestInstanceFromBeanHolder(IJExpression fieldRef, IJExpression contextRef, JBlock block) {
		AbstractJClass beanHolderClass = getJClass(BeanHolder.class);

		JBlock ifBlock = block._if(fieldRef._instanceof(beanHolderClass))._then();
		JVar beanHolderVar = ifBlock.decl(beanHolderClass, "beanHolder", cast(beanHolderClass, fieldRef));
		JVar beanVar = ifBlock.decl(getGeneratedClass(), "bean", beanHolderVar.invoke("getBean").arg(getGeneratedClass().dotclass()));

		JInvocation newBeanExpression = _new(getGeneratedClass()).arg(contextRef);
		if (fieldRef != contextRef) {
			newBeanExpression = newBeanExpression.arg(fieldRef);
		}

		JBlock ifBeanNullBlock = ifBlock._if(beanVar.eq(_null()))._then();
		ifBeanNullBlock.assign(beanVar, newBeanExpression);
		ifBeanNullBlock.add(beanHolderVar.invoke("putBean").arg(getGeneratedClass().dotclass()).arg(beanVar));
		ifBeanNullBlock.invoke(beanVar, getInit());

		ifBlock._return(beanVar);
	}

	public void createRebindMethod() {
		JMethod rebindMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "rebind");
		JVar contextParam = rebindMethod.param(getClasses().CONTEXT, "context");
		JBlock body = rebindMethod.body();
		body.assign(getContextField(), contextParam);
		body.invoke(getInit());
	}
}
