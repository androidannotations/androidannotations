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

import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.generation.ItemClickInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

/**
 * @author Benjamin Fellous
 */
public class ItemClickProcessor implements ElementProcessor {

	private final IRClass rClass;

	public ItemClickProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemClick.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		ItemClick annotation = element.getAnnotation(ItemClick.class);
		int idValue = annotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.ID);
		String itemClickQualifiedId;

		if (idValue == LongClick.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			itemClickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
		} else {
			itemClickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
		}

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();

		Instruction instruction = new ItemClickInstruction(methodName, itemClickQualifiedId);
		onCreateInstructions.add(instruction);

	}

}
