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

import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.generation.RoboActivityBeforeCreateInstruction;
import com.googlecode.androidannotations.generation.RoboActivityBodyInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JCodeModel;

public class RoboGuiceProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return RoboGuice.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		MetaActivity metaActivity = metaModel.getMetaActivities().get(element);

		Instruction onBeforeCreateInstruction = new RoboActivityBeforeCreateInstruction();

		List<String> listenerClasses = extractListenerClasses(element);
		Instruction memberInstruction = new RoboActivityBodyInstruction(listenerClasses);

		List<Instruction> onBeforeCreateInstructions = metaActivity.getBeforeCreateInstructions();
		onBeforeCreateInstructions.add(onBeforeCreateInstruction);

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

						for (Object value : values) {
							listenerClasses.add(value.toString());
						}
						return listenerClasses;

					}

				}
			}
		}

		return new ArrayList<String>(0);

	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		// TODO Auto-generated method stub
		
	}

}
