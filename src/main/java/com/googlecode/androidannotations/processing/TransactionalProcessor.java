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
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.generation.TransactionalInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JCodeModel;

public class TransactionalProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Transactional.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		String className = metaActivity.getClassSimpleName();
		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();

		List<String> methodArguments = new ArrayList<String>();
		List<String> methodParameters = new ArrayList<String>();

		ExecutableElement executableElement = (ExecutableElement) element;

		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			String parameterType = parameter.asType().toString();
			methodArguments.add(parameterType + " " + parameterName);
			methodParameters.add(parameterName);
		}

		String returnType = executableElement.getReturnType().toString();

		Instruction instruction = new TransactionalInstruction(methodName, className, methodArguments, methodParameters, returnType);
		memberInstructions.add(instruction);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		// TODO Auto-generated method stub
		
	}

}
