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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.Enhance;
import com.googlecode.androidannotations.generation.StartActivityInstruction;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class EnhanceProcessor extends ValidatorHelper implements ElementProcessor {

	private final IRClass rClass;

	public EnhanceProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Enhance.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		TypeElement typeElement = (TypeElement) element;

		Enhance layoutAnnotation = element.getAnnotation(Enhance.class);
		int layoutIdValue = layoutAnnotation.value();
		
		String layoutFieldQualifiedName;
		if (layoutIdValue != Enhance.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			layoutFieldQualifiedName = rInnerClass.getIdQualifiedName(layoutIdValue);
		} else {
			layoutFieldQualifiedName = null;
		}

		String superClassQualifiedName = typeElement.getQualifiedName().toString();

		int packageSeparatorIndex = superClassQualifiedName.lastIndexOf('.');

		String packageName = superClassQualifiedName.substring(0, packageSeparatorIndex);

		String superClassSimpleName = superClassQualifiedName.substring(packageSeparatorIndex + 1);

		MetaActivity activity = new MetaActivity(packageName, superClassSimpleName, layoutFieldQualifiedName);

		activity.getMemberInstructions().add(new StartActivityInstruction());

		metaModel.getMetaActivities().put(element, activity);
	}

}
