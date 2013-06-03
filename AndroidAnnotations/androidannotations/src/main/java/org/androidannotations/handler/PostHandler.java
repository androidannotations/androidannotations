package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.TreeMap;

public class PostHandler extends RestMethodHandler {

	public PostHandler(ProcessingEnvironment processingEnvironment) {
		super(Post.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.doesNotReturnPrimitive((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Post annotation = element.getAnnotation(Post.class);
		return annotation.value();
	}

	@Override
	protected JExpression getRequestEntity(Element element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> methodParams) {
		String mediaType = restAnnotationHelper.acceptedHeaders((ExecutableElement) element);
		JVar httpRestHeaders = null;
		if (mediaType != null) {
			httpRestHeaders = restAnnotationHelper.declareAcceptedHttpHeaders(holder, methodBody, mediaType);
		}
		return restAnnotationHelper.declareHttpEntity(holder, methodBody, methodParams, httpRestHeaders);
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
