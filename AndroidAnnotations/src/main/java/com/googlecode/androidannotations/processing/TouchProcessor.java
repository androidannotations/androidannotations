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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.generation.TouchInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.sun.codemodel.JCodeModel;

/**
 * @author Pierre-Yves Ricau
 */
public class TouchProcessor implements ElementProcessor {

	private final IRClass rClass;

	public TouchProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Touch.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		Touch annotation = element.getAnnotation(Touch.class);
		int idValue = annotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.ID);
		String longClickQualifiedId;

		if (idValue == Id.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			int lastIndex = fieldName.lastIndexOf("Touched");
			if (lastIndex != -1) {
				fieldName = fieldName.substring(0, lastIndex);
			}
			longClickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
		} else {
			longClickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
		}

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean viewParameter = parameters.size() == 2;

		TypeMirror returnType = executableElement.getReturnType();

		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		Instruction instruction = new TouchInstruction(methodName, longClickQualifiedId, viewParameter, returnMethodResult);
		onCreateInstructions.add(instruction);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		// TODO Auto-generated method stub
		
	}
}
