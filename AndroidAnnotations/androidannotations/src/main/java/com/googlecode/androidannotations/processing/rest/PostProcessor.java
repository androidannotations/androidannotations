package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.helper.ProcessorConstants;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class PostProcessor extends MethodProcessor {

	public PostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Post.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;
		
		TypeMirror returnType = executableElement.getReturnType();
		
		JClass generatedReturnType;
		String returnTypeString = returnType.toString();
		String restMethodName;
		JClass expectedClass;
		
		if (returnTypeString.startsWith(ProcessorConstants.URI)) {
			restMethodName = "postForLocation";
			DeclaredType declaredReturnedType = (DeclaredType) returnType;
			TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
			expectedClass = holder.refClass(typeParameter.toString());
			generatedReturnType = holder.refClass(ProcessorConstants.URI);
		} else if (returnTypeString.startsWith(ProcessorConstants.RESPONSE_ENTITY)) {
			restMethodName = "postForEntity";
			DeclaredType declaredReturnedType = (DeclaredType) returnType;
			TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
			expectedClass = holder.refClass(typeParameter.toString());
			generatedReturnType = holder.refClass(ProcessorConstants.RESPONSE_ENTITY).narrow(expectedClass);
		} else {
			restMethodName = "postForObject";
			generatedReturnType = holder.refClass(returnTypeString);
			expectedClass = generatedReturnType;
		}

		Post postAnnotation = element.getAnnotation(Post.class);
		String urlSuffix = postAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;
		
		createGeneratedRestCallBlock(executableElement, url, restMethodName, expectedClass, generatedReturnType, codeModel);

	}

}
