package com.googlecode.androidannotations.helper;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.processing.EActivityProcessor;
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

		Integer idValue = annotationHelper.extractAnnotationValue(element);

		if (idValue.equals(Id.DEFAULT_VALUE)) {
			if (defaultUseName) {
				String methodName = element.getSimpleName().toString();
				int lastIndex = methodName.lastIndexOf(annotationHelper.actionName());
				if (lastIndex != -1) {
					methodName = methodName.substring(0, lastIndex);
				}
				if (!idAnnotationHelper.containsField(methodName, res)) {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "Id not found: R." + res.rName() + "." + methodName);
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
			String annotationQualifiedId = idAnnotationHelper.extractAnnotationQualifiedId(element);

			Set<? extends Element> annotatedElements = validatedElements.getAnnotatedElements(annotationHelper.getTarget());
			for (Element uniqueCheckElement : annotatedElements) {
				Element enclosingElement = uniqueCheckElement.getEnclosingElement();
				if (layoutElement.equals(enclosingElement)) {
					String checkQualifiedId = idAnnotationHelper.extractAnnotationQualifiedId(uniqueCheckElement);
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

	public void idListenerMethod(Element element, AnnotationElements validatedElements, IsValid valid) {
		enclosingElementHasEActivity(element, validatedElements, valid);

		idExists(element, Res.ID, valid);

		isNotPrivate(element, valid);

		doesntThrowException(element, valid);

		uniqueId(element, validatedElements, valid);
	}

	public void activityRegistered(Element element, AndroidManifest androidManifest, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		String activityQualifiedName = typeElement.getQualifiedName().toString();
		String generatedActivityQualifiedName = activityQualifiedName + EActivityProcessor.NEW_CLASS_SUFFIX;

		List<String> activityQualifiedNames = androidManifest.getActivityQualifiedNames();
		if (!activityQualifiedNames.contains(generatedActivityQualifiedName)) {
			String simpleName = typeElement.getSimpleName().toString();
			String generatedSimpleName = simpleName + EActivityProcessor.NEW_CLASS_SUFFIX;
			if (activityQualifiedNames.contains(activityQualifiedName)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The AndroidManifest.xml file contains the original activity, and not the AndroidAnnotations generated activity. Please register " + generatedSimpleName + " instead of " + simpleName);
			} else {
				annotationHelper.printAnnotationWarning(element, "The activity " + generatedSimpleName + " is not registered in the AndroidManifest.xml file.");
			}
		}

	}
}
