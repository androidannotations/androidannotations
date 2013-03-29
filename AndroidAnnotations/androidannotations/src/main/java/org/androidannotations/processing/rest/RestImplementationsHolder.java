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
package org.androidannotations.processing.rest;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

public class RestImplementationsHolder {

	private Map<Element, RestImplementationHolder> holders = new HashMap<Element, RestImplementationHolder>();

	public RestImplementationHolder create(Element element) {
		RestImplementationHolder holder = new RestImplementationHolder();
		holders.put(element, holder);
		return holder;
	}

	public RestImplementationHolder getEnclosingHolder(Element enclosedElement) {
		Element activityElement = enclosedElement.getEnclosingElement();
		return holders.get(activityElement);
	}

	public RestImplementationHolder getRelativeHolder(Element element) {
		return holders.get(element);
	}

}
