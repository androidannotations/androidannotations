/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import org.androidannotations.rclass.IRInnerClass;

import com.sun.codemodel.JFieldRef;

public class IdAnnotationHelper extends TargetAnnotationHelper {

	private final IRClass rClass;

	public IdAnnotationHelper(ProcessingEnvironment processingEnv, String annotationName, IRClass rClass) {
		super(processingEnv, annotationName);
		this.rClass = rClass;
	}

	public boolean containsIdValue(Integer idValue, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsIdValue(idValue);
	}

	public boolean containsField(String name, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsField(name);
	}

	public List<String> extractAnnotationResources(Element element, Res res, boolean useElementName) {
		return super.extractAnnotationResources(element, getTarget(), rClass.get(res), useElementName);
	}

	public List<JFieldRef> extractAnnotationFieldRefs(ProcessHolder holder, Element element, Res res, boolean useElementName) {
		return extractAnnotationFieldRefs(holder, element, res, useElementName, DEFAULT_FIELD_NAME_VALUE, DEFAULT_FIELD_NAME_RESNAME);
	}

	public List<JFieldRef> extractAnnotationFieldRefs(ProcessHolder holder, Element element, Res res, boolean useElementName, String idFieldName, String resFieldName) {
		return super.extractAnnotationFieldRefs(holder, element, getTarget(), rClass.get(res), useElementName, idFieldName, resFieldName);
	}

	public JFieldRef extractOneAnnotationFieldRef(ProcessHolder holder, Element element, Res res, boolean useElementName) {
		return extractOneAnnotationFieldRef(holder, element, getTarget(), res, useElementName);
	}

	public JFieldRef extractOneAnnotationFieldRef(ProcessHolder holder, Element element, String annotationName, Res res, boolean useElementName) {
		return extractOneAnnotationFieldRef(holder, element, annotationName, res, useElementName, DEFAULT_FIELD_NAME_VALUE, DEFAULT_FIELD_NAME_RESNAME);
	}

	public JFieldRef extractOneAnnotationFieldRef(ProcessHolder holder, Element element, String annotationName, Res res, boolean useElementName, String idFieldName, String resFieldName) {
		return extractOneAnnotationFieldRef(holder, element, annotationName, rClass.get(res), useElementName, idFieldName, resFieldName);
	}

	public JFieldRef extractOneAnnotationFieldRef(ProcessHolder holder, Element element, String annotationName, IRInnerClass rInnerClass, boolean useElementName, String idFieldName,
			String resFieldName) {
		List<JFieldRef> jFieldRefs = extractAnnotationFieldRefs(holder, element, annotationName, rInnerClass, useElementName, idFieldName, resFieldName);

		if (jFieldRefs.size() == 1) {
			return jFieldRefs.get(0);
		} else {
			return null;
		}
	}
}
