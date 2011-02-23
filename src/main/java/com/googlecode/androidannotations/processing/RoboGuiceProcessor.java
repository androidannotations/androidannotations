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
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.generation.RoboActivityBodyInstruction;
import com.googlecode.androidannotations.generation.RoboActivityOnCreateInstruction;
import com.googlecode.androidannotations.helper.RoboGuiceConstants;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;

public class RoboGuiceProcessor implements ElementProcessor {

	private final Elements elementUtils;

	public RoboGuiceProcessor(Elements elementUtils) {
		this.elementUtils = elementUtils;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return RoboGuice.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		MetaActivity metaActivity = metaModel.getMetaActivities().get(element);

		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();
		Instruction onCreateInstruction = new RoboActivityOnCreateInstruction();
		onCreateInstructions.add(onCreateInstruction);

		boolean roboGuice10 = elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_1_0_APPLICATION_CLASS) != null;

		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();

		Instruction memberInstruction = new RoboActivityBodyInstruction(roboGuice10);
		memberInstructions.add(memberInstruction);

		List<String> implementedInterfaces = metaActivity.getImplementedInterfaces();
		implementedInterfaces.add("roboguice.inject.InjectorProvider");

	}

}
