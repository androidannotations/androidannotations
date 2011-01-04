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
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Value;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.generation.ColorValueInstruction;
import com.googlecode.androidannotations.generation.StringArrayValueInstruction;
import com.googlecode.androidannotations.generation.StringValueInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.googlecode.androidannotations.rclass.RInnerClass;

public class ValueProcessor implements ElementProcessor {

	private static final String INTEGER_TYPE = "java.lang.Integer";
	private static final String INT_TYPE = "int";
	private static final String STRING_TYPE = "java.lang.String";

	private final RClass rClass;

	public ValueProcessor(RClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Value.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String name = element.getSimpleName().toString();

		TypeMirror uiFieldTypeMirror = element.asType();
		String qualifiedName = uiFieldTypeMirror.toString();

		Value annotation = element.getAnnotation(Value.class);
		int idValue = annotation.value();

		Res resInnerClass;
		if (qualifiedName.equals(STRING_TYPE)) {
			resInnerClass = Res.STRING;
		} else if (qualifiedName.equals(INT_TYPE) || qualifiedName.equals(INTEGER_TYPE)) {
			resInnerClass = Res.COLOR;
		} else {
			resInnerClass = Res.ARRAY;
		}

		RInnerClass rInnerClass = rClass.get(resInnerClass);
		String qualifiedId;
		if (idValue == ViewById.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			qualifiedId = rInnerClass.getIdQualifiedName(fieldName);
		} else {
			qualifiedId = rInnerClass.getIdQualifiedName(idValue);
		}

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();

		Instruction instruction;
		switch (resInnerClass) {
		case STRING:
			instruction = new StringValueInstruction(name, qualifiedId);
			break;
		case COLOR:
			instruction = new ColorValueInstruction(name, qualifiedId);
			break;
		default:
			instruction = new StringArrayValueInstruction(name, qualifiedId);
		}

		onCreateInstructions.add(instruction);

	}

}
