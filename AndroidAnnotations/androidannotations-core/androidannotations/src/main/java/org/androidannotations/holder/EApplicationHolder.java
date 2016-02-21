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

import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EApplicationHolder extends EComponentHolder {

	public static final String GET_APPLICATION_INSTANCE = "getInstance";

	private JFieldVar staticInstanceField;

	public EApplicationHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		createSingleton();
		createOnCreate();
	}

	private void createSingleton() {
		AbstractJClass annotatedComponent = generatedClass._extends();

		staticInstanceField = generatedClass.field(PRIVATE | STATIC, annotatedComponent, "INSTANCE" + generationSuffix());
		// Static singleton getter and setter
		JMethod getInstance = generatedClass.method(PUBLIC | STATIC, annotatedComponent, GET_APPLICATION_INSTANCE);
		getInstance.body()._return(staticInstanceField);

		JMethod setInstance = generatedClass.method(PUBLIC | STATIC, getCodeModel().VOID, "setForTesting");
		setInstance.javadoc().append("Visible for testing purposes");
		JVar applicationParam = setInstance.param(annotatedComponent, "application");
		setInstance.body().assign(staticInstanceField, applicationParam);
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreate");
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
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
	}
}
