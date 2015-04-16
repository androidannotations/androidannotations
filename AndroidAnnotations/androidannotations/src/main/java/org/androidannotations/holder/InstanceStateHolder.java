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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class InstanceStateHolder extends GeneratedClassHolderDecorator<EComponentHolder> implements HasInstanceState {

	private JBlock saveStateMethodBody;
	private JVar saveStateBundleParam;
	private JMethod restoreStateMethod;
	private JVar restoreStateBundleParam;

	public InstanceStateHolder(EComponentHolder holder) {
		super(holder);
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		if (saveStateMethodBody == null) {
			setSaveStateMethod();
		}
		return saveStateMethodBody;
	}

	@Override
	public JVar getSaveStateBundleParam() {
		if (saveStateBundleParam == null) {
			setSaveStateMethod();
		}
		return saveStateBundleParam;
	}

	private void setSaveStateMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().VOID, "onSaveInstanceState");
		method.annotate(Override.class);
		saveStateBundleParam = method.param(classes().BUNDLE, "bundle_");

		saveStateMethodBody = method.body();

		saveStateMethodBody.invoke(JExpr._super(), "onSaveInstanceState").arg(saveStateBundleParam);
	}

	@Override
	public JMethod getRestoreStateMethod() {
		if (restoreStateMethod == null) {
			setRestoreStateMethod();
		}
		return restoreStateMethod;
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		if (restoreStateBundleParam == null) {
			setRestoreStateMethod();
		}
		return restoreStateBundleParam;
	}

	private void setRestoreStateMethod() {
		restoreStateMethod = getGeneratedClass().method(PRIVATE, codeModel().VOID, "restoreSavedInstanceState_");
		restoreStateBundleParam = restoreStateMethod.param(classes().BUNDLE, "savedInstanceState");
		getInit().body().invoke(restoreStateMethod).arg(restoreStateBundleParam);

		restoreStateMethod.body() //
				._if(ref("savedInstanceState").eq(_null())) //
				._then()._return();
	}

	public JMethod getInit() {
		return holder.getInit();
	}

	@Override
	public JDefinedClass getGeneratedClass() {
		return holder.getGeneratedClass();
	}

	@Override
	public JCodeModel codeModel() {
		return holder.codeModel();
	}

	@Override
	public ProcessHolder.Classes classes() {
		return holder.classes();
	}

	@Override
	public TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	@Override
	public ProcessingEnvironment processingEnvironment() {
		return holder.processingEnvironment();
	}

	@Override
	public JClass refClass(String fullyQualifiedClassName) {
		return holder.refClass(fullyQualifiedClassName);
	}

	@Override
	public JClass refClass(Class<?> clazz) {
		return holder.refClass(clazz);
	}

	@Override
	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		return holder.definedClass(fullyQualifiedClassName);
	}
}
