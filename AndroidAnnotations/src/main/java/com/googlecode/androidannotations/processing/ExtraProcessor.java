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

import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.generation.ExtraInstruction;
import com.googlecode.androidannotations.generation.ExtractAndCastExtraInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JCodeModel;

public class ExtraProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Extra.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {
		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);

		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();

		Instruction extractAndCastExtraInstruction = new ExtractAndCastExtraInstruction();
		if (!memberInstructions.contains(extractAndCastExtraInstruction)) {
			memberInstructions.add(extractAndCastExtraInstruction);
		}

		String className = metaActivity.getClassSimpleName();
		String fieldName = element.getSimpleName().toString();

		Extra annotation = element.getAnnotation(Extra.class);
		String key = annotation.value();

		List<Instruction> beforeCreateInstructions = metaActivity.getBeforeCreateInstructions();

		Instruction instruction = new ExtraInstruction(className, fieldName, key);
		beforeCreateInstructions.add(instruction);

	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		// TODO Auto-generated method stub
		
	}

}
