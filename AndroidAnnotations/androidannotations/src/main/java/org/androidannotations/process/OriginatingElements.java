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
package org.androidannotations.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

public class OriginatingElements {

	private final Map<String, List<Element>> originatingElementsByClassName = new HashMap<>();

	public void add(String qualifiedName, Element element) {
		List<Element> originatingElements = originatingElementsByClassName.get(qualifiedName);
		if (originatingElements == null) {
			originatingElements = new ArrayList<>();
			originatingElementsByClassName.put(qualifiedName, originatingElements);
		}
		originatingElements.add(element);
	}

	public Element[] getClassOriginatingElements(String className) {
		List<Element> originatingElements = originatingElementsByClassName.get(className);
		if (originatingElements == null) {
			return new Element[0];
		} else {
			return originatingElements.toArray(new Element[originatingElements.size()]);
		}
	}
}
