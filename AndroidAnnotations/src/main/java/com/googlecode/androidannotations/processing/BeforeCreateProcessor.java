/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.BeforeCreate;
import com.googlecode.androidannotations.generation.BeforeCreateInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

public class BeforeCreateProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return BeforeCreate.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<Instruction> beforeCreateInstructions = metaActivity.getBeforeCreateInstructions();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean bundleParameter = parameters.size() == 1;

		Instruction instruction = new BeforeCreateInstruction(methodName, bundleParameter);
		beforeCreateInstructions.add(instruction);

	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		
		ActivityHolder holder = activitiesHolder.getActivityHolder(element);
		
		String methodName = element.getSimpleName().toString();
		
		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean hasBundleParameter = parameters.size() == 1;

		JInvocation methodCall = holder.beforeSetContentView.body().invoke(methodName);
		
		if (hasBundleParameter) {
			methodCall.arg(holder.beforeSetContentViewSavedInstanceStateParam);
		}
	}

}
