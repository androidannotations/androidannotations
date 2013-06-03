package org.androidannotations.handler.rest;

import com.sun.codemodel.*;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.RestAnnotationHelper;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public abstract class RestMethodHandler extends BaseAnnotationHandler<RestHolder> {

	protected final RestAnnotationHelper restAnnotationHelper;
	protected final APTCodeModelHelper codeModelHelper;

	public RestMethodHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
		restAnnotationHelper = new RestAnnotationHelper(processingEnv, getTarget());
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.notAlreadyValidated(element, validatedElements, valid);

		validatorHelper.enclosingElementHasRestAnnotation(element, validatedElements, valid);

		validatorHelper.throwsOnlyRestClientException((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();
		JClass methodReturnClass = getMethodReturnClass(element, holder);

		// Creating method signature
		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, methodReturnClass, methodName);
		method.annotate(Override.class);
		TreeMap <String, JVar> params = addMethodParams(executableElement, holder, method);
		JBlock methodBody = method.body();

		// RestTemplate exchange() method call
		JInvocation exchangeCall = JExpr.invoke(holder.getRestTemplateField(), "exchange");
		exchangeCall.arg(getUrl(element, holder));
		exchangeCall.arg(getHttpMethod(holder));
		exchangeCall.arg(getRequestEntity(element, holder, methodBody, params));
		exchangeCall.arg(getResponseClass(element, holder));
		JExpression urlVariables = getUrlVariables(element, holder, methodBody, params);
		if (urlVariables != null)
			exchangeCall.arg(urlVariables);

		// Call exchange()
		if (executableElement.getReturnType().getKind() == TypeKind.VOID) {
			methodBody.add(exchangeCall);
		} else {
			exchangeCall = addResultCallMethod(exchangeCall, methodReturnClass);
			methodBody._return(exchangeCall);
		}
	}

	protected JClass getMethodReturnClass(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		return codeModelHelper.typeMirrorToJClass(executableElement.getReturnType(), holder);
	}

	protected TreeMap<String, JVar> addMethodParams(ExecutableElement executableElement, RestHolder restHolder, JMethod method) {
		List<? extends VariableElement> params = executableElement.getParameters();
		TreeMap<String, JVar> methodParams = new TreeMap<String, JVar>();
		for (VariableElement parameter : params) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();

			JVar param;
			if (parameter.asType().getKind().isPrimitive()) {
				param = method.param(JType.parse(restHolder.codeModel(), paramType), paramName);
			} else {
				JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType(), restHolder);
				param = method.param(parameterClass, paramName);
			}
			methodParams.put(paramName, param);
		}
		return methodParams;
	}

	protected JExpression getUrl(Element element, RestHolder restHolder) {
		return JExpr.invoke(restHolder.getRootUrlField(), "concat").arg(JExpr.lit(getUrlSuffix(element)));
	}

	protected abstract String getUrlSuffix(Element element);

	protected JExpression getHttpMethod(RestHolder holder) {
		JClass httpMethod = holder.classes().HTTP_METHOD;
		String simpleName = getTarget().substring(getTarget().lastIndexOf('.') + 1);
		String restMethodInCapitalLetters = simpleName.toUpperCase(Locale.ENGLISH);
		return httpMethod.staticRef(restMethodInCapitalLetters);
	}

	protected JExpression getRequestEntity(Element element, RestHolder restHolder, JBlock methodBody, TreeMap<String, JVar> params) {
		return JExpr._null();
	}

	protected JExpression getResponseClass(Element element, RestHolder holder) {
		return JExpr._null();
	}

	protected JExpression getUrlVariables(Element element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> params) {
		return restAnnotationHelper.declareUrlVariables((ExecutableElement) element, holder, methodBody, params);
	}

	protected JInvocation addResultCallMethod(JInvocation exchangeCall, JClass methodReturnClass) {
		return exchangeCall;
	}
}
