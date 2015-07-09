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

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;

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
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init" + generationSuffix());
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
