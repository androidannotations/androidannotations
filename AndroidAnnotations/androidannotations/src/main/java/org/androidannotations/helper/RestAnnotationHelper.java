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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
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
import org.androidannotations.annotations.rest.RequiresAuthentication;
import org.androidannotations.annotations.rest.RequiresCookie;
import org.androidannotations.annotations.rest.RequiresCookieInUrl;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.SetsCookie;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class RestAnnotationHelper extends TargetAnnotationHelper {

	private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public RestAnnotationHelper(ProcessingEnvironment processingEnv, String annotationName) {
		super(processingEnv, annotationName);
	}

	public void urlVariableNamesExistInParameters(ExecutableElement element, Set<String> variableNames, IsValid valid) {

		List<? extends VariableElement> parameters = element.getParameters();

		List<String> parametersName = new ArrayList<>();
		for (VariableElement parameter : parameters) {
			parametersName.add(parameter.getSimpleName().toString());
		}

		String[] cookiesToUrl = requiredUrlCookies(element);
		if (cookiesToUrl != null) {
			for (String cookie : cookiesToUrl) {
				parametersName.add(cookie);
			}
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

		Set<String> variableNames = new HashSet<>();
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

	public JVar declareUrlVariables(ExecutableElement element, RestHolder holder, JBlock methodBody, SortedMap<String, JVar> methodParams) {
		Set<String> urlVariables = extractUrlVariableNames(element);

		// cookies in url?
		String[] cookiesToUrl = requiredUrlCookies(element);
		if (cookiesToUrl != null) {
			for (String cookie : cookiesToUrl) {
				urlVariables.add(cookie);
			}
		}

		JClass hashMapClass = holder.classes().HASH_MAP.narrow(String.class, Object.class);
		if (!urlVariables.isEmpty()) {
			JVar hashMapVar = methodBody.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));
			for (String urlVariable : urlVariables) {
				JVar methodParam = methodParams.get(urlVariable);
				if (methodParam != null) {
					methodBody.invoke(hashMapVar, "put").arg(urlVariable).arg(methodParam);
					methodParams.remove(urlVariable);
				} else {
					// cookie from url
					JInvocation cookieValue = holder.getAvailableCookiesField().invoke("get").arg(JExpr.lit(urlVariable));
					methodBody.invoke(hashMapVar, "put").arg(urlVariable).arg(cookieValue);
				}
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

	public String[] requiredHeaders(ExecutableElement executableElement) {
		RequiresHeader cookieAnnotation = executableElement.getAnnotation(RequiresHeader.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresHeader.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	public String[] requiredCookies(ExecutableElement executableElement) {
		RequiresCookie cookieAnnotation = executableElement.getAnnotation(RequiresCookie.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresCookie.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	public static String[] requiredUrlCookies(ExecutableElement executableElement) {
		RequiresCookieInUrl cookieAnnotation = executableElement.getAnnotation(RequiresCookieInUrl.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresCookieInUrl.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	public String[] settingCookies(ExecutableElement executableElement) {
		SetsCookie cookieAnnotation = executableElement.getAnnotation(SetsCookie.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(SetsCookie.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	public boolean requiredAuthentication(ExecutableElement executableElement) {
		RequiresAuthentication basicAuthAnnotation = executableElement.getAnnotation(RequiresAuthentication.class);
		if (basicAuthAnnotation == null) {
			basicAuthAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresAuthentication.class);
		}
		return basicAuthAnnotation != null;
	}

	public JVar declareHttpHeaders(ExecutableElement executableElement, RestHolder holder, JBlock body) {
		JVar httpHeadersVar = null;

		String mediaType = acceptedHeaders(executableElement);
		boolean hasMediaTypeDefined = mediaType != null;

		String[] cookies = requiredCookies(executableElement);
		boolean requiresCookies = cookies != null && cookies.length > 0;

		String[] headers = requiredHeaders(executableElement);
		boolean requiresHeaders = headers != null && headers.length > 0;

		boolean requiresAuth = requiredAuthentication(executableElement);

		if (hasMediaTypeDefined || requiresCookies || requiresHeaders || requiresAuth) {
			// we need the headers
			httpHeadersVar = body.decl(holder.classes().HTTP_HEADERS, "httpHeaders", JExpr._new(holder.classes().HTTP_HEADERS));
		}

		if (hasMediaTypeDefined) {
			JClass collectionsClass = holder.refClass(CanonicalNameConstants.COLLECTIONS);
			JClass mediaTypeClass = holder.refClass(CanonicalNameConstants.MEDIA_TYPE);

			JInvocation mediaTypeListParam = collectionsClass.staticInvoke("singletonList").arg(mediaTypeClass.staticInvoke("parseMediaType").arg(mediaType));
			body.add(JExpr.invoke(httpHeadersVar, "setAccept").arg(mediaTypeListParam));
		}

		if (requiresCookies) {
			JClass stringBuilderClass = holder.classes().STRING_BUILDER;
			JVar cookiesValueVar = body.decl(stringBuilderClass, "cookiesValue", JExpr._new(stringBuilderClass));
			for (String cookie : cookies) {
				JInvocation cookieValue = JExpr.invoke(holder.getAvailableCookiesField(), "get").arg(cookie);
				JInvocation cookieFormatted = holder.classes().STRING.staticInvoke("format").arg(String.format("%s=%%s;", cookie)).arg(cookieValue);
				JInvocation appendCookie = JExpr.invoke(cookiesValueVar, "append").arg(cookieFormatted);
				body.add(appendCookie);
			}

			JInvocation cookiesToString = cookiesValueVar.invoke("toString");
			body.add(JExpr.invoke(httpHeadersVar, "set").arg("Cookie").arg(cookiesToString));
		}

		if (requiresHeaders) {
			for (String header : headers) {
				JInvocation headerValue = JExpr.invoke(holder.getAvailableHeadersField(), "get").arg(header);
				body.add(JExpr.invoke(httpHeadersVar, "set").arg(header).arg(headerValue));
			}

		}

		if (requiresAuth) {
			// attach auth
			body.add(httpHeadersVar.invoke("setAuthorization").arg(holder.getAuthenticationField()));
		}

		return httpHeadersVar;
	}

	public JVar getEntitySentToServer(ExecutableElement element, SortedMap<String, JVar> params) {
		Set<String> urlVariables = extractUrlVariableNames(element);
		for (String paramName : params.keySet()) {
			if (!urlVariables.contains(paramName)) {
				return params.get(paramName);
			}
		}
		return null;
	}

	public JExpression declareHttpEntity(ProcessHolder holder, JBlock body, JVar entitySentToServer, JVar httpHeaders) {
		JType entityType = holder.refClass(Object.class);

		if (entitySentToServer != null) {
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
			return JExpr._null();
		}

		return body.decl(narrowedHttpEntity, "requestEntity", newHttpEntityVarCall);
	}

	public JExpression getResponseClass(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		JExpression responseClassExpr = nullCastedToNarrowedClass(holder);
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
	 * <li>One of the parent is a {@link java.util.Map Map} : Generate a
	 * subclass of {@link LinkedHashMap}&lt;T&gt; one and return it</li>
	 * <li>One of the parent is a {@link Set} : Generate a subclass of
	 * {@link TreeSet}&lt;T&gt; one and return it</li>
	 * <li>One of the parent is a {@link java.util.Collection Collection} :
	 * Generate a subclass of {@link ArrayList}&lt;T&gt; one and return it</li>
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
	 * <code>enclosingJClass</code> is {@link java.util.Map Map}, {@link Set} or
	 * {@link java.util.Collection Collection}.
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
				TypeMirror actualTypeArgument = typeArgument;
				if (typeArgument instanceof WildcardType) {
					WildcardType wildcardType = (WildcardType) typeArgument;
					if (wildcardType.getExtendsBound() != null) {
						actualTypeArgument = wildcardType.getExtendsBound();
					} else if (wildcardType.getSuperBound() != null) {
						actualTypeArgument = wildcardType.getSuperBound();
					}
				}
				JClass narrowJClass = codeModelHelper.typeMirrorToJClass(actualTypeArgument, holder);
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

	public JExpression nullCastedToNarrowedClass(RestHolder holder) {
		return JExpr.cast(holder.refClass(Class.class).narrow(holder.refClass(Void.class)), JExpr._null());
	}
}
