package org.androidannotations.handler.rest;

import com.sun.codemodel.*;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.TreeMap;

public class GetHandler extends RestMethodHandler {

	public GetHandler(ProcessingEnvironment processingEnvironment) {
		super(Get.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.doesNotReturnPrimitive((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Get annotation = element.getAnnotation(Get.class);
		return annotation.value();
	}

	@Override
	protected JExpression getRequestEntity(Element element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> methodParams) {
		ExecutableElement executableElement = (ExecutableElement) element;
		String mediaType = restAnnotationHelper.acceptedHeaders(executableElement);
		if (mediaType != null) {
			JClass httpEntity = holder.classes().HTTP_ENTITY;
			JInvocation newHttpEntityVarCall = JExpr._new(httpEntity.narrow(Object.class));
			JVar httpHeaders = restAnnotationHelper.declareAcceptedHttpHeaders(holder, methodBody, mediaType);
			newHttpEntityVarCall.arg(httpHeaders);
			return methodBody.decl(httpEntity.narrow(Object.class), "requestEntity", newHttpEntityVarCall);
		}
		return JExpr._null();
	}

	protected JExpression getResponseClass(Element element, RestHolder holder) {
		return restAnnotationHelper.getResponseClass(element, holder);
	}

	protected JInvocation addResultCallMethod(JInvocation exchangeCall, JClass methodReturnClass) {
		if (methodReturnClass != null && !methodReturnClass.fullName().startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			return JExpr.invoke(exchangeCall, "getBody");
		}
		return exchangeCall;
	}
}
