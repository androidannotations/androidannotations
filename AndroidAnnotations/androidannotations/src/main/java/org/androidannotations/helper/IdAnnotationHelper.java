/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.processing.EBeanHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import org.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JFieldRef;

public class IdAnnotationHelper extends TargetAnnotationHelper {

	private final IRClass rClass;

	public IdAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target, IRClass rClass) {
		super(processingEnv, target);
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

	public List<JFieldRef> extractAnnotationFieldRefs(EBeanHolder holder, Element element, Res res, boolean useElementName) {
		return super.extractAnnotationFieldRefs(holder, element, getTarget(), rClass.get(res), useElementName);
	}

	public JFieldRef extractOneAnnotationFieldRef(EBeanHolder holder, Element element, Res res, boolean useElementName) {
		List<JFieldRef> jFieldRefs = extractAnnotationFieldRefs(holder, element, res, useElementName);

		if (jFieldRefs.size() == 1) {
			return jFieldRefs.get(0);
		} else {
			return null;
		}

	}

}
