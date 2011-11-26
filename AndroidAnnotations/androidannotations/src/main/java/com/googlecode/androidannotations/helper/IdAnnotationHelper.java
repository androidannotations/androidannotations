/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;

public class IdAnnotationHelper extends TargetAnnotationHelper {

	private final IRClass rClass;

	public IdAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target, IRClass rClass) {
		super(processingEnv, target);
		this.rClass = rClass;
	}

	public List<String> extractAnnotationQualifiedIds(Element element) {
		int[] idsValues = extractAnnotationValue(element);
		IRInnerClass rInnerClass = rClass.get(Res.ID);
		List<String> clickQualifiedIds = new ArrayList<String>();

		if (idsValues.length == 1 && idsValues[0] == Id.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			int lastIndex = fieldName.lastIndexOf(actionName());
			if (lastIndex != -1) {
				fieldName = fieldName.substring(0, lastIndex);
			}
			String clickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
			clickQualifiedIds.add(clickQualifiedId);

		} else {
			for (int idValue : idsValues) {
				String clickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
				clickQualifiedIds.add(clickQualifiedId);
			}
		}
		return clickQualifiedIds;
	}

	boolean containsIdValue(Integer idValue, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsIdValue(idValue);
	}

	boolean containsField(String name, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsField(name);
	}

}
