/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;

public class EProviderHolder extends EComponentHolder {

	private JMethod onCreateMethod;
	private JBlock onCreateBody;

	public EProviderHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		contextRef = invoke("getContext");
	}

	@Override
	protected void setInit() {
		initMethod = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		createOnCreate();
	}

	private void createOnCreate() {
		onCreateMethod = generatedClass.method(PUBLIC, getCodeModel().BOOLEAN, "onCreate");
		onCreateMethod.annotate(Override.class);
		onCreateBody = onCreateMethod.body();
		onCreateBody.invoke(getInit());
		onCreateBody._return(invoke(_super(), onCreateMethod));
	}

	public JMethod getOnCreate() {
		return onCreateMethod;
	}

	public JBlock getOnCreateBody() {
		return onCreateBody;
	}
}
