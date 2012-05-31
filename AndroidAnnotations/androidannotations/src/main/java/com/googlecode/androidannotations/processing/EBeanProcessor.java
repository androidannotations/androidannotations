/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EBeanProcessor implements ElementProcessor {

	public static final String GET_INSTANCE_METHOD_NAME = "getInstance" + GENERATION_SUFFIX;

	@Override
	public Class<? extends Annotation> getTarget() {
		return EBean.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		EBeanHolder holder = eBeansHolder.create(element);

		TypeElement typeElement = (TypeElement) element;

		String eBeanQualifiedName = typeElement.getQualifiedName().toString();

		String generatedBeanQualifiedName = eBeanQualifiedName + GENERATION_SUFFIX;

		holder.eBean = codeModel._class(PUBLIC | FINAL, generatedBeanQualifiedName, ClassType.CLASS);

		JClass eBeanClass = codeModel.directClass(eBeanQualifiedName);

		holder.eBean._extends(eBeanClass);

		Classes classes = holder.classes();

		JFieldVar contextField = holder.eBean.field(PRIVATE, classes.CONTEXT, "context_");

		holder.contextRef = contextField;

		{
			// afterSetContentView

			holder.afterSetContentView = holder.eBean.method(PUBLIC, codeModel.VOID, "afterSetContentView_");

			JBlock afterSetContentViewBody = holder.afterSetContentView.body();

			afterSetContentViewBody._if(holder.contextRef._instanceof(classes.ACTIVITY).not())._then()._return();
		}

		{
			// findViewById

			JMethod findViewById = holder.eBean.method(PUBLIC, classes.VIEW, "findViewById");
			JVar idParam = findViewById.param(codeModel.INT, "id");

			findViewById.javadoc().add("You should check that context is an activity before calling this method");

			JBlock findViewByIdBody = findViewById.body();

			JVar activityVar = findViewByIdBody.decl(classes.ACTIVITY, "activity_", cast(classes.ACTIVITY, holder.contextRef));

			findViewByIdBody._return(activityVar.invoke(findViewById).arg(idParam));
		}

		{
			// init
			holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
		}

		{
			// init if activity
			/*
			 * We suppress all warnings because we generate an unused warning
			 * that may or may not valid
			 */
			holder.init.annotate(SuppressWarnings.class).param("value", "all");
			APTCodeModelHelper helper = new APTCodeModelHelper();
			holder.initIfActivityBody = helper.ifContextInstanceOfActivity(holder, holder.init.body());
			holder.initActivityRef = helper.castContextToActivity(holder, holder.initIfActivityBody);
		}

		{
			// Constructor

			JMethod constructor = holder.eBean.constructor(PRIVATE);

			JVar constructorContextParam = constructor.param(classes.CONTEXT, "context");

			JBlock constructorBody = constructor.body();

			constructorBody.assign(contextField, constructorContextParam);

			constructorBody.invoke(holder.init);
		}

		EBean eBeanAnnotation = element.getAnnotation(EBean.class);
		Scope eBeanScope = eBeanAnnotation.scope();
		boolean hasSingletonScope = eBeanScope == Scope.Singleton;

		{
			// Factory method

			JMethod factoryMethod = holder.eBean.method(PUBLIC | STATIC, holder.eBean, GET_INSTANCE_METHOD_NAME);

			JVar factoryMethodContextParam = factoryMethod.param(classes.CONTEXT, "context");

			JBlock factoryMethodBody = factoryMethod.body();

			/*
			 * Singletons are bound to the application context
			 */
			if (hasSingletonScope) {

				JFieldVar instanceField = holder.eBean.field(PRIVATE | STATIC, holder.eBean, "instance_");

				factoryMethodBody //
						._if(instanceField.eq(_null())) //
						._then() //
						.assign(instanceField, _new(holder.eBean).arg(factoryMethodContextParam.invoke("getApplicationContext")));

				factoryMethodBody._return(instanceField);
			} else {
				factoryMethodBody._return(_new(holder.eBean).arg(factoryMethodContextParam));
			}
		}

		{
			// rebind(Context)
			JMethod rebindMethod = holder.eBean.method(PUBLIC, codeModel.VOID, "rebind");
			JVar contextParam = rebindMethod.param(classes.CONTEXT, "context");

			/*
			 * No rebinding of context for singletons, their are bound to the
			 * application context
			 */
			if (!hasSingletonScope) {
				JBlock body = rebindMethod.body();
				body.assign(contextField, contextParam);
				body.invoke(holder.init);
			}
		}

	}
}
