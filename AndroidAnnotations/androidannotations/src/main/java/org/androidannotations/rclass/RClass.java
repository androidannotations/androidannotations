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
package org.androidannotations.rclass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

public class RClass implements IRClass {

	private final Map<String, RInnerClass> rClass = new HashMap<>();

	public RClass(TypeElement rClassElement) {
		List<TypeElement> rInnerTypeElements = extractRInnerTypeElements(rClassElement);

		for (TypeElement rInnerTypeElement : rInnerTypeElements) {
			RInnerClass rInnerClass = new RInnerClass(rInnerTypeElement);
			rClass.put(rInnerTypeElement.getSimpleName().toString(), rInnerClass);
		}
	}

	private List<TypeElement> extractRInnerTypeElements(TypeElement rClassElement) {
		List<? extends Element> rEnclosedElements = rClassElement.getEnclosedElements();
		return ElementFilter.typesIn(rEnclosedElements);
	}

	@Override
	public IRInnerClass get(Res res) {

		String id = res.rName();

		IRInnerClass rInnerClass = rClass.get(id);
		if (rInnerClass != null) {
			return rInnerClass;
		} else {
			return IRInnerClass.EMPTY_R_INNER_CLASS;
		}
	}
}
