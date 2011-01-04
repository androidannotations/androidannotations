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

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.generation.BackgroundInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;

public class BackgroundProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Background.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		String className = metaActivity.getClassSimpleName();
		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();

		Instruction instruction = new BackgroundInstruction(className, methodName);
		memberInstructions.add(instruction);
	}

}
