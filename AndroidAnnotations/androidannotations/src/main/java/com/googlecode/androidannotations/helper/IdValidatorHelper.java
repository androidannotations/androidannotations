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

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.validation.IsValid;

public class IdValidatorHelper extends ValidatorHelper {

	private final IdAnnotationHelper idAnnotationHelper;

	public IdValidatorHelper(IdAnnotationHelper idAnnotationHelper) {
		super(idAnnotationHelper);
		this.idAnnotationHelper = idAnnotationHelper;
	}

	public void idExists(Element element, Res res, IsValid valid) {
		idExists(element, res, true, valid);
	}

	public void idExists(Element element, Res res, boolean defaultUseName, IsValid valid) {
		idExists(element, res, defaultUseName, true, valid);
	}

	public void idExists(Element element, Res res, boolean defaultUseName, boolean allowDefault, IsValid valid) {

		Integer idValue = annotationHelper.extractAnnotationValue(element);

		idExists(element, res, defaultUseName, allowDefault, valid, idValue);
	}

	public void idsExists(Element element, Res res, IsValid valid) {

		int[] idsValues = annotationHelper.extractAnnotationValue(element);

		if (idsValues == null) {
			valid.invalidate();
			annotationHelper.printAnnotationWarning(element, "The value of the %s annotation could not be determined at compile time, for unknown reasons. Please report this issue.");
		} else if (idsValues[0] == Id.DEFAULT_VALUE) {
			idExists(element, res, true, true, valid, idsValues[0]);
		} else {
			for (int idValue : idsValues) {
				idExists(element, res, false, true, valid, idValue);
			}
		}
	}

	private void idExists(Element element, Res res, boolean defaultUseName, boolean allowDefault, IsValid valid, Integer idValue) {
		if (allowDefault && idValue.equals(Id.DEFAULT_VALUE)) {
			if (defaultUseName) {
				String elementName = element.getSimpleName().toString();
				int lastIndex = elementName.lastIndexOf(annotationHelper.actionName());
				if (lastIndex != -1) {
					elementName = elementName.substring(0, lastIndex);
				}
				if (!idAnnotationHelper.containsField(elementName, res)) {
					valid.invalidate();
					String message;
					String snakeCaseName = CaseHelper.camelCaseToSnakeCase(elementName);
					String rQualifiedPrefix = String.format("R.%s.", res.rName());
					if (snakeCaseName.equals(elementName)) {
						message = "Id not found: " + rQualifiedPrefix + elementName;
					} else {
						message = "Id not found: " + rQualifiedPrefix + elementName + " or " + rQualifiedPrefix + snakeCaseName;
					}
					annotationHelper.printAnnotationError(element, message);
				}
			}
		} else {
			if (!idAnnotationHelper.containsIdValue(idValue, res)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "Id value not found in R." + res.rName() + ": " + idValue);
			}
		}
	}

	public void uniqueId(Element element, AnnotationElements validatedElements, IsValid valid) {

		if (valid.isValid()) {
			Element layoutElement = element.getEnclosingElement();
			List<String> annotationQualifiedIds = idAnnotationHelper.extractAnnotationQualifiedIds(element);

			Set<? extends Element> annotatedElements = validatedElements.getAnnotatedElements(annotationHelper.getTarget());

			for (Element uniqueCheckElement : annotatedElements) {
				Element enclosingElement = uniqueCheckElement.getEnclosingElement();

				if (layoutElement.equals(enclosingElement)) {
					List<String> checkQualifiedIds = idAnnotationHelper.extractAnnotationQualifiedIds(uniqueCheckElement);

					for (String checkQualifiedId : checkQualifiedIds) {
						for (String annotationQualifiedId : annotationQualifiedIds) {

							if (annotationQualifiedId.equals(checkQualifiedId)) {
								valid.invalidate();
								String annotationSimpleId = annotationQualifiedId.substring(annotationQualifiedId.lastIndexOf('.') + 1);
								annotationHelper.printAnnotationError(element, "The id " + annotationSimpleId + " is already used on the following " + annotationHelper.annotationName() + " method: " + uniqueCheckElement);
								return;
							}
						}
					}
				}
			}
		}
	}

}
