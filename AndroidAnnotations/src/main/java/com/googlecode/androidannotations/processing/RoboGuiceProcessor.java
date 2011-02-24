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
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.generation.Robo10ActivityBeforeCreateInstruction;
import com.googlecode.androidannotations.generation.Robo10ActivityBodyInstruction;
import com.googlecode.androidannotations.generation.Robo10ActivityOnCreateInstruction;
import com.googlecode.androidannotations.generation.Robo11ActivityBeforeCreateInstruction;
import com.googlecode.androidannotations.generation.Robo11ActivityBodyInstruction;
import com.googlecode.androidannotations.generation.Robo11ActivityOnCreateInstruction;
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

		boolean roboGuice10 = elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_1_0_APPLICATION_CLASS) != null;

		Instruction onBeforeCreateInstruction;
		Instruction onCreateInstruction;
		Instruction memberInstruction;
		if (roboGuice10) {
			onBeforeCreateInstruction = new Robo10ActivityBeforeCreateInstruction();
			onCreateInstruction = new Robo10ActivityOnCreateInstruction();
			memberInstruction = new Robo10ActivityBodyInstruction();
		} else {
			onBeforeCreateInstruction = new Robo11ActivityBeforeCreateInstruction();
			onCreateInstruction = new Robo11ActivityOnCreateInstruction();

			List<String> listenerClasses = extractListenerClasses(element);
			memberInstruction = new Robo11ActivityBodyInstruction(listenerClasses);
		}

		List<Instruction> onBeforeCreateInstructions = metaActivity.getBeforeCreateInstructions();
		onBeforeCreateInstructions.add(onBeforeCreateInstruction);

		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();
		onCreateInstructions.add(onCreateInstruction);

		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();
		memberInstructions.add(memberInstruction);

		List<String> implementedInterfaces = metaActivity.getImplementedInterfaces();
		implementedInterfaces.add("roboguice.inject.InjectorProvider");
	}
	

	private List<String> extractListenerClasses(Element activityElement) {
		
		List<? extends AnnotationMirror> annotationMirrors = activityElement.getAnnotationMirrors();

		String annotationName = RoboGuice.class.getName();
		AnnotationValue action = null;
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationName.equals(annotationMirror.getAnnotationType().toString())) {
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					if ("value".equals(entry.getKey().getSimpleName().toString())) {
						action = entry.getValue();
						
						@SuppressWarnings("unchecked")
						List<Object> values = (List<Object>) action.getValue();
						
						List<String> listenerClasses = new ArrayList<String>();
						
						for(Object value : values) {
							listenerClasses.add(value.toString());
						}
						return listenerClasses;

					}

				}
			}
		}
		
		return new ArrayList<String>(0);

	}

}
