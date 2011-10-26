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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

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

	public void idExists(Element element, Res res, boolean defaultUseName,
			IsValid valid) {

		Integer idValue = annotationHelper.extractAnnotationValue(element);

		if (idValue.equals(Id.DEFAULT_VALUE)) {
			if (defaultUseName) {
				String elementName = element.getSimpleName().toString();
				int lastIndex = elementName.lastIndexOf(annotationHelper
						.actionName());
				if (lastIndex != -1) {
					elementName = elementName.substring(0, lastIndex);
				}
				if (!idAnnotationHelper.containsField(elementName, res)) {
					valid.invalidate();
					String message;
					String snakeCaseName = CaseHelper
							.camelCaseToSnakeCase(elementName);
					String rQualifiedPrefix = String.format("R.%s.",
							res.rName());
					if (snakeCaseName.equals(elementName)) {
						message = "Id not found: " + rQualifiedPrefix
								+ elementName;
					} else {
						message = "Id not found: " + rQualifiedPrefix
								+ elementName + " or " + rQualifiedPrefix
								+ snakeCaseName;
					}
					annotationHelper.printAnnotationError(element, message);
				}
			}
		} else {
			if (!idAnnotationHelper.containsIdValue(idValue, res)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element,
						"Id value not found in R." + res.rName() + ": "
								+ idValue);
			}
		}
	}

	public void uniqueId(Element element, AnnotationElements validatedElements,
			IsValid valid) {
		if (valid.isValid()) {
			Element layoutElement = element.getEnclosingElement();
			String annotationQualifiedId = idAnnotationHelper
					.extractAnnotationQualifiedId(element);

			Set<? extends Element> annotatedElements = validatedElements
					.getAnnotatedElements(annotationHelper.getTarget());
			for (Element uniqueCheckElement : annotatedElements) {
				Element enclosingElement = uniqueCheckElement
						.getEnclosingElement();
				if (layoutElement.equals(enclosingElement)) {
					String checkQualifiedId = idAnnotationHelper
							.extractAnnotationQualifiedId(uniqueCheckElement);
					if (annotationQualifiedId.equals(checkQualifiedId)) {
						valid.invalidate();
						String annotationSimpleId = annotationQualifiedId
								.substring(annotationQualifiedId
										.lastIndexOf('.') + 1);
						annotationHelper.printAnnotationError(element,
								"The id " + annotationSimpleId
										+ " is already used on the following "
										+ annotationHelper.annotationName()
										+ " method: " + uniqueCheckElement);
						return;
					}
				}
			}
		}
	}

	public void idListenerMethod(Element element,
			AnnotationElements validatedElements, IsValid valid) {


        enclosingElementHasEBeanAnnotation(element, validatedElements, valid);
        
		idExists(element, Res.ID, valid);

		isNotPrivate(element, valid);

		doesntThrowException((ExecutableElement) element, valid);

		uniqueId(element, validatedElements, valid);
	}

	public void activityRegistered(Element element,
			AndroidManifest androidManifest, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		String activityQualifiedName = typeElement.getQualifiedName()
				.toString();
		String generatedActivityQualifiedName = activityQualifiedName
				+ ModelConstants.GENERATION_SUFFIX;

		List<String> activityQualifiedNames = androidManifest
				.getActivityQualifiedNames();
		if (!activityQualifiedNames.contains(generatedActivityQualifiedName)) {
			String simpleName = typeElement.getSimpleName().toString();
			String generatedSimpleName = simpleName
					+ ModelConstants.GENERATION_SUFFIX;
			if (activityQualifiedNames.contains(activityQualifiedName)) {
				valid.invalidate();
				annotationHelper
						.printAnnotationError(
								element,
								"The AndroidManifest.xml file contains the original activity, and not the AndroidAnnotations generated activity. Please register "
										+ generatedSimpleName
										+ " instead of "
										+ simpleName);
			} else {
				annotationHelper
						.printAnnotationWarning(
								element,
								"The activity "
										+ generatedSimpleName
										+ " is not registered in the AndroidManifest.xml file.");
			}
		}

	}

}
