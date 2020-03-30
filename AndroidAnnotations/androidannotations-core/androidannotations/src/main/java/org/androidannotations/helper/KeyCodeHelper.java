/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;

public class KeyCodeHelper extends TargetAnnotationHelper {

	public static final String KEYCODE_PREFIX = "KEYCODE";
	public static final int KEYCODE_NOT_FOUND = -1;

	private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("^(on)?(.+?)(Pressed)?$");

	private Map<String, Integer> keyCodesByName;
	private Map<Integer, String> keyNamesByCode;

	public KeyCodeHelper(AndroidAnnotationsEnvironment environment, String annotationName) {
		super(environment, annotationName);
	}

	public int[] extractKeyCode(Element element) {
		int[] value = extractAnnotationValueParameter(element);
		if (value.length == 0) {
			Matcher matcher = METHOD_NAME_PATTERN.matcher(element.getSimpleName());
			if (matcher.matches()) {
				String extractedKeyName = matcher.group(2).toLowerCase();
				String constantName = CaseHelper.camelCaseToUpperSnakeCase(KEYCODE_PREFIX, extractedKeyName, "");
				int keyCode = getKeyCodeForName(constantName);
				if (keyCode == KEYCODE_NOT_FOUND) {
					return value;
				} else {
					value = new int[] { keyCode };
				}
			}
		}
		return value;
	}

	public int getKeyCodeForName(String fieldName) {
		if (keyCodesByName == null) {
			keyCodesByName = new HashMap<>();
			List<VariableElement> keyEventEnclosedFieldElements = getKeyEventEnclosedFieldElements();
			for (VariableElement element : keyEventEnclosedFieldElements) {
				if (element.getSimpleName().toString().contains(KEYCODE_PREFIX)) {
					keyCodesByName.put(element.getSimpleName().toString(), (Integer) element.getConstantValue());
				}
			}
		}
		Integer keyCode = keyCodesByName.get(fieldName);
		return keyCode != null ? keyCode : KEYCODE_NOT_FOUND;
	}

	public String getFieldNameForKeyCode(int keyCode) {
		if (keyNamesByCode == null) {
			keyNamesByCode = new HashMap<>();
			List<VariableElement> keyEventEnclosedFieldElements = getKeyEventEnclosedFieldElements();
			for (VariableElement element : keyEventEnclosedFieldElements) {
				if (element.getSimpleName().toString().contains(KEYCODE_PREFIX)) {
					keyNamesByCode.put((Integer) element.getConstantValue(), element.getSimpleName().toString());
				}
			}
		}
		return keyNamesByCode.get(keyCode);
	}

	private List<VariableElement> getKeyEventEnclosedFieldElements() {
		TypeElement keyEventElement = getElementUtils().getTypeElement(CanonicalNameConstants.KEY_EVENT);
		return ElementFilter.fieldsIn(keyEventElement.getEnclosedElements());
	}

	public boolean uniqueKeyCode(Element element, String targetAnnotationClass) {
		int[] elementsKeyCodes = extractKeyCode(element);
		if (elementsKeyCodes.length == 0) {
			return false;
		}

		Set<Integer> uniqueKeyCodes = new HashSet<>(elementsKeyCodes.length);
		for (int keyCode : elementsKeyCodes) {
			uniqueKeyCodes.add(keyCode);
		}
		Element enclosingElement = element.getEnclosingElement();
		List<? extends Element> enclosedMethodElements = ElementFilter.methodsIn(enclosingElement.getEnclosedElements());
		for (Element oneEnclosedElement : enclosedMethodElements) {
			if (oneEnclosedElement != element) {
				List<? extends AnnotationMirror> annotationMirrors = oneEnclosedElement.getAnnotationMirrors();
				for (AnnotationMirror annotationMirror : annotationMirrors) {
					if (annotationMirror.getAnnotationType().asElement().toString().equals(targetAnnotationClass)) {
						int[] keyCodes = extractKeyCode(oneEnclosedElement);
						for (int keyCode : keyCodes) {
							if (uniqueKeyCodes.contains(keyCode)) {
								return false;
							} else {
								uniqueKeyCodes.add(keyCode);
							}
						}
					}
				}
			}
		}
		return true;
	}
}
