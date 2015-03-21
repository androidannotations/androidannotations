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
import java.util.Set;

import javax.lang.model.element.Element;

import org.androidannotations.annotations.ResId;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass.Res;

public class IdValidatorHelper extends ValidatorHelper {

	private final IdAnnotationHelper idAnnotationHelper;

	public IdValidatorHelper(IdAnnotationHelper idAnnotationHelper) {
		super(idAnnotationHelper);
		this.idAnnotationHelper = idAnnotationHelper;
	}

	public enum FallbackStrategy {
		USE_ELEMENT_NAME, ALLOW_NO_RES_ID, NEED_RES_ID
	}

	public void resIdsExist(Element element, Res res, FallbackStrategy fallbackStrategy, IsValid valid) {

		String annotationName = idAnnotationHelper.getTarget();
		int[] resIds = idAnnotationHelper.extractAnnotationResIdValueParameter(element, annotationName);

		if (idAnnotationHelper.defaultResIdValue(resIds)) {
			String[] resNames = idAnnotationHelper.extractAnnotationResNameParameter(element, annotationName);

			if (idAnnotationHelper.defaultResName(resNames)) {
				if (fallbackStrategy == FallbackStrategy.USE_ELEMENT_NAME) {
					/*
					 * fallback, using element name
					 */
					String elementName = idAnnotationHelper.extractElementName(element, annotationName);

					if (!idAnnotationHelper.containsField(elementName, res)) {
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "Resource name not found in R." + res.rName() + ": " + elementName);
					}
				} else if (fallbackStrategy == FallbackStrategy.NEED_RES_ID) {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "%s needs an annotation value");
				}
			} else {
				for (String resName : resNames) {
					if (!idAnnotationHelper.containsField(resName, res)) {
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "Resource name not found in R." + res.rName() + ": " + resName);
					}
				}
			}
		} else {
			for (int resId : resIds) {
				if (!idAnnotationHelper.containsIdValue(resId, res)) {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "Resource id value not found in R." + res.rName() + ": " + resId);
				}
			}
		}
	}

	public void annotationParameterIsOptionalValidResId(Element element, Res res, String parameterName, IsValid valid) {
		Integer resId = annotationHelper.extractAnnotationParameter(element, parameterName);
		if (!resId.equals(ResId.DEFAULT_VALUE) && !idAnnotationHelper.containsIdValue(resId, res)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Id value not found in R." + res.rName() + ": " + resId);
		}
	}

	public void uniqueResourceId(Element element, AnnotationElements validatedElements, Res resourceType, IsValid valid) {

		if (valid.isValid()) {

			List<String> annotationQualifiedIds = idAnnotationHelper.extractAnnotationResources(element, resourceType, true);

			Element elementEnclosingElement = element.getEnclosingElement();
			Set<? extends Element> annotatedElements = validatedElements.getRootAnnotatedElements(annotationHelper.getTarget());

			for (Element uniqueCheckElement : annotatedElements) {
				Element uniqueCheckEnclosingElement = uniqueCheckElement.getEnclosingElement();

				if (elementEnclosingElement.equals(uniqueCheckEnclosingElement)) {

					List<String> checkQualifiedIds = idAnnotationHelper.extractAnnotationResources(uniqueCheckElement, resourceType, true);

					for (String checkQualifiedId : checkQualifiedIds) {
						for (String annotationQualifiedId : annotationQualifiedIds) {

							if (annotationQualifiedId.equals(checkQualifiedId)) {
								valid.invalidate();
								String annotationSimpleId = annotationQualifiedId.substring(annotationQualifiedId.lastIndexOf('.') + 1);
								annotationHelper.printAnnotationError(element, "The resource id " + annotationSimpleId + " is already used on the following " + annotationHelper.annotationName()
										+ " method: " + uniqueCheckElement);
								return;
							}
						}
					}
				}
			}
		}
	}

	public void uniqueId(Element element, AnnotationElements validatedElements, IsValid valid) {
		uniqueResourceId(element, validatedElements, Res.ID, valid);
	}

	public void annotationValuePositiveAndInAShort(Element element, IsValid valid, int value) {
		if (value < 0 || value > 0xFFFF) {
			annotationHelper.printAnnotationError(element, "Due to a restriction in the fragment API, the requestCode has to be a positive integer inferior or equal to 0xFFFF");
			valid.invalidate();
		}
	}
}
