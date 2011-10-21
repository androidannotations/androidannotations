package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.validation.IsValid;

public class RestAnnotationHelper extends TargetAnnotationHelper {

	public RestAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target) {
		super(processingEnv, target);
	}

	public void urlVariableNamesExistInParameters(ExecutableElement element, List<String> variableNames, IsValid valid) {

		List<? extends VariableElement> parameters = element.getParameters();

		List<String> parametersName = new ArrayList<String>();
		for (VariableElement parameter : parameters) {
			parametersName.add(parameter.getSimpleName().toString());
		}

		for (String variableName : variableNames) {
			if (!parametersName.contains(variableName)) {
				valid.invalidate();
				printAnnotationError(element, "%s annotated method has an url variable which name could not be found in the method parameters: " + variableName);
				return;
			}
		}
	}

	public void urlVariableNamesExistInParameters(ExecutableElement element, IsValid valid) {
		if (valid.isValid()) {
			List<String> variableNames = extractUrlVariableNames(element);
			urlVariableNamesExistInParameters(element, variableNames, valid);
		}
	}

	public void urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter(ExecutableElement element, IsValid valid) {
		if (valid.isValid()) {
			List<String> variableNames = extractUrlVariableNames(element);
			urlVariableNamesExistInParameters(element, variableNames, valid);
			if (valid.isValid()) {
				List<? extends VariableElement> parameters = element.getParameters();

				if (parameters.size() > variableNames.size() + 1) {
					valid.invalidate();
					printAnnotationError(element, "%s annotated method has more than one entity parameter");
				}
			}
		}
	}

	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	public List<String> extractUrlVariableNames(ExecutableElement element) {

		// extract variables name from root url isn't really useful
		
//		Element enclosingElement = element.getEnclosingElement();
//		String urlPrefix = enclosingElement.getAnnotation(Rest.class).value();
//		String urlSuffix = extractAnnotationValue(element);
//		String uriTemplate = urlPrefix + urlSuffix;
		
		String uriTemplate = extractAnnotationValue(element);

		Matcher m = NAMES_PATTERN.matcher(uriTemplate);
		List<String> variableNames = new ArrayList<String>();
		while (m.find()) {
			variableNames.add(m.group(1));
		}

		return variableNames;
	}

}
