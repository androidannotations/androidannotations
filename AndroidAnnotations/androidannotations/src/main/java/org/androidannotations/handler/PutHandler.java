package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.handler.rest.RestMethodHandler;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.TreeMap;

public class PutHandler extends RestMethodHandler {

	public PutHandler(ProcessingEnvironment processingEnvironment) {
		super(Put.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Put annotation = element.getAnnotation(Put.class);
		return annotation.value();
	}

	@Override
	protected JExpression getRequestEntity(Element element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> methodParams) {
		return restAnnotationHelper.declareHttpEntity(holder, methodBody, methodParams);
	}
}
