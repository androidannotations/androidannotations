/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.androidannotations.process.ProcessHolder;

public class RestAnnotationHelper extends TargetAnnotationHelper {

	private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public RestAnnotationHelper(ProcessingEnvironment processingEnv, String annotationName) {
		super(processingEnv, annotationName);
	}

	public void urlVariableNamesExistInParameters(ExecutableElement element, Set<String> variableNames, IsValid valid) {

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

	public void urlVariableNamesExistInParametersAndHasNoOneMoreParameter(ExecutableElement element, IsValid valid) {
		if (valid.isValid()) {
			Set<String> variableNames = extractUrlVariableNames(element);
			urlVariableNamesExistInParameters(element, variableNames, valid);
			if (valid.isValid()) {
				List<? extends VariableElement> parameters = element.getParameters();

				if (parameters.size() > variableNames.size()) {
					valid.invalidate();
					printAnnotationError(element, "%s annotated method has only url variables in the method parameters");
				}
			}
		}
	}

	public void urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter(ExecutableElement element, IsValid valid) {
		if (valid.isValid()) {
			Set<String> variableNames = extractUrlVariableNames(element);
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

	public Set<String> extractUrlVariableNames(ExecutableElement element) {

		Set<String> variableNames = new HashSet<String>();
		String uriTemplate = extractAnnotationValueParameter(element);

		boolean hasValueInAnnotation = uriTemplate != null;
		if (hasValueInAnnotation) {
			Matcher m = NAMES_PATTERN.matcher(uriTemplate);
			while (m.find()) {
				variableNames.add(m.group(1));
			}
		}

		return variableNames;
	}

	public JVar declareUrlVariables(ExecutableElement element, ProcessHolder holder, JBlock methodBody, TreeMap<String, JVar> methodParams) {
		Set<String> urlVariables = extractUrlVariableNames(element);
		JClass hashMapClass = holder.refClass(HashMap.class).narrow(String.class, Object.class);
		if (!urlVariables.isEmpty()) {
			JVar hashMapVar = methodBody.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));
			for (String urlVariable : urlVariables) {
				JVar urlValue = methodParams.get(urlVariable);
				methodBody.invoke(hashMapVar, "put").arg(urlVariable).arg(urlValue);
				methodParams.remove(urlVariable);
			}
			return hashMapVar;
		}
		return null;
	}

	public String acceptedHeaders(ExecutableElement executableElement) {
		Accept acceptAnnotation = executableElement.getAnnotation(Accept.class);
		if (acceptAnnotation == null) {
			acceptAnnotation = executableElement.getEnclosingElement().getAnnotation(Accept.class);
		}
		if (acceptAnnotation != null) {
			return acceptAnnotation.value();
		} else {
			return null;
		}
	}

	public JVar declareAcceptedHttpHeaders(ProcessHolder holder, JBlock body, String mediaType) {
		JClass httpHeadersClass = holder.classes().HTTP_HEADERS;
		JClass collectionsClass = holder.classes().COLLECTIONS;
		JClass mediaTypeClass = holder.classes().MEDIA_TYPE;

		JVar httpHeadersVar = body.decl(httpHeadersClass, "httpHeaders", JExpr._new(httpHeadersClass));
		JInvocation mediaTypeListParam = collectionsClass.staticInvoke("singletonList").arg(mediaTypeClass.staticInvoke("parseMediaType").arg(mediaType));
		body.add(JExpr.invoke(httpHeadersVar, "setAccept").arg(mediaTypeListParam));

		return httpHeadersVar;
	}

	public JExpression declareHttpEntity(ProcessHolder holder, JBlock body, TreeMap<String, JVar> methodParams) {
		return declareHttpEntity(holder, body, methodParams, null);
	}

	public JExpression declareHttpEntity(ProcessHolder holder, JBlock body, TreeMap<String, JVar> methodParams, JVar httpHeaders) {
		JVar entitySentToServer = null;
		JType entityType = holder.refClass(Object.class);

		if (!methodParams.isEmpty()) {
			entitySentToServer = methodParams.firstEntry().getValue();
			entityType = entitySentToServer.type();
			if (entityType.isPrimitive()) {
				// Don't narrow primitive types...
				entityType = entityType.boxify();
			}
		}

		JClass httpEntity = holder.classes().HTTP_ENTITY;
		JClass narrowedHttpEntity = httpEntity.narrow(entityType);
		JInvocation newHttpEntityVarCall = JExpr._new(narrowedHttpEntity);

		if (entitySentToServer != null) {
			newHttpEntityVarCall.arg(entitySentToServer);
		}

		if (httpHeaders != null) {
			newHttpEntityVarCall.arg(httpHeaders);
		} else if (entitySentToServer == null) {
			newHttpEntityVarCall.arg(JExpr._null());
		}

		return body.decl(narrowedHttpEntity, "requestEntity", newHttpEntityVarCall);
	}

	public JExpression getResponseClass(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		JExpression responseClassExpr = JExpr._null();
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() != TypeKind.VOID) {
			JClass responseClass = retrieveResponseClass(returnType, holder);
			if (responseClass != null) {
				responseClassExpr = responseClass.dotclass();
			}
		}
		return responseClassExpr;
	}

	public JClass retrieveResponseClass(TypeMirror returnType, RestHolder holder) {
		String returnTypeString = returnType.toString();

		JClass responseClass;

		if (returnTypeString.startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			DeclaredType declaredReturnType = (DeclaredType) returnType;
			if (declaredReturnType.getTypeArguments().size() > 0) {
				responseClass = resolveResponseClass(declaredReturnType.getTypeArguments().get(0), holder);
			} else {
				responseClass = holder.classes().RESPONSE_ENTITY;
			}
		} else {
			responseClass = resolveResponseClass(returnType, holder);
		}

		return responseClass;
	}

	/**
	 * Resolve the expected class for the input type according to the following
	 * rules :
	 * <ul>
	 * <li>The type is a primitive : Directly return the JClass as usual</li>
	 * <li>The type is NOT a generics : Directly return the JClass as usual</li>
	 * <li>The type is a generics and enclosing type is a class C&lt;T&gt; :
	 * Generate a subclass of C&lt;T&gt; and return it</li>
	 * <li>The type is a generics and enclosing type is an interface I&lt;T&gt;
	 * : Looking the inheritance tree, then</li>
	 * <ol>
	 * <li>One of the parent is a {@link Map} : Generate a subclass of
	 * {@link LinkedHashMap}&lt;T&gt; one and return it</li>
	 * <li>One of the parent is a {@link Set} : Generate a subclass of
	 * {@link TreeSet}&lt;T&gt; one and return it</li>
	 * <li>One of the parent is a {@link Collection} : Generate a subclass of
	 * {@link ArrayList}&lt;T&gt; one and return it</li>
	 * <li>Return {@link Object} definition</li>
	 * </ol>
	 * </ul>
	 * 
	 */
	private JClass resolveResponseClass(TypeMirror expectedType, RestHolder holder) {
		// is a class or an interface
		if (expectedType.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredType = (DeclaredType) expectedType;

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			// is NOT a generics, return directly
			if (typeArguments.isEmpty()) {
				return codeModelHelper.typeMirrorToJClass(declaredType, holder);
			}

			// is a generics, must generate a new super class
			TypeElement declaredElement = (TypeElement) declaredType.asElement();

			JClass baseClass = codeModelHelper.typeMirrorToJClass(declaredType, holder).erasure();
			JClass decoratedExpectedClass = retrieveDecoratedResponseClass(declaredType, declaredElement, holder);
			if (decoratedExpectedClass == null) {
				decoratedExpectedClass = baseClass;
			}
			return decoratedExpectedClass;
		} else if (expectedType.getKind() == TypeKind.ARRAY) {
			ArrayType arrayType = (ArrayType) expectedType;
			return resolveResponseClass(arrayType.getComponentType(), holder).array();
		}

		// is not a class nor an interface, return directly
		return codeModelHelper.typeMirrorToJClass(expectedType, holder);
	}

	/**
	 * Recursive method used to find if one of the grand-parent of the
	 * <code>enclosingJClass</code> is {@link Map}, {@link Set} or
	 * {@link Collection}.
	 */
	private JClass retrieveDecoratedResponseClass(DeclaredType declaredType, TypeElement typeElement, RestHolder holder) {
		String classTypeBaseName = typeElement.toString();

		// Looking for basic java.util interfaces to set a default
		// implementation
		String decoratedClassName = null;

		if (typeElement.getKind() == ElementKind.INTERFACE) {
			if (classTypeBaseName.equals(CanonicalNameConstants.MAP)) {
				decoratedClassName = LinkedHashMap.class.getCanonicalName();
			} else if (classTypeBaseName.equals(CanonicalNameConstants.SET)) {
				decoratedClassName = TreeSet.class.getCanonicalName();
			} else if (classTypeBaseName.equals(CanonicalNameConstants.LIST)) {
				decoratedClassName = ArrayList.class.getCanonicalName();
			} else if (classTypeBaseName.equals(CanonicalNameConstants.COLLECTION)) {
				decoratedClassName = ArrayList.class.getCanonicalName();
			}
		} else {
			decoratedClassName = typeElement.getQualifiedName().toString();
		}

		if (decoratedClassName != null) {
			// Configure the super class of the final decorated class
			String decoratedClassNameSuffix = "";
			JClass decoratedSuperClass = holder.refClass(decoratedClassName);
			for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
				if (typeArgument instanceof WildcardType) {
					WildcardType wildcardType = (WildcardType) typeArgument;
					if (wildcardType.getExtendsBound() != null) {
						typeArgument = wildcardType.getExtendsBound();
					} else if (wildcardType.getSuperBound() != null) {
						typeArgument = wildcardType.getSuperBound();
					}
				}
				JClass narrowJClass = codeModelHelper.typeMirrorToJClass(typeArgument, holder);
				decoratedSuperClass = decoratedSuperClass.narrow(narrowJClass);
				decoratedClassNameSuffix += plainName(narrowJClass);
			}

			String decoratedFinalClassName = classTypeBaseName + "_" + decoratedClassNameSuffix;
			decoratedFinalClassName = decoratedFinalClassName.replaceAll("\\[\\]", "s");
			String packageName = holder.getGeneratedClass()._package().name();
			decoratedFinalClassName = packageName + "." + decoratedFinalClassName;
			JDefinedClass decoratedJClass = holder.definedClass(decoratedFinalClassName);
			decoratedJClass._extends(decoratedSuperClass);

			return decoratedJClass;
		}

		// Try to find the superclass and make a recursive call to the this
		// method
		TypeMirror enclosingSuperJClass = typeElement.getSuperclass();
		if (enclosingSuperJClass != null && enclosingSuperJClass.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredEnclosingSuperJClass = (DeclaredType) enclosingSuperJClass;
			return retrieveDecoratedResponseClass(declaredType, (TypeElement) declaredEnclosingSuperJClass.asElement(), holder);
		}

		// Falling back to the current enclosingJClass if Class can't be found
		return null;
	}

	protected String plainName(JClass jClass) {
		String plainName = jClass.erasure().name();
		List<JClass> typeParameters = jClass.getTypeParameters();
		if (typeParameters.size() > 0) {
			plainName += "_";
			for (JClass typeParameter : typeParameters) {
				plainName += plainName(typeParameter);
			}
		}
		return plainName;
	}
}
