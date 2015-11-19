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
package org.androidannotations.rest.spring.helper;

import static org.androidannotations.rest.spring.helper.RestSpringClasses.HTTP_ENTITY;
import static org.androidannotations.rest.spring.helper.RestSpringClasses.HTTP_HEADERS;
import static org.androidannotations.rest.spring.helper.RestSpringClasses.MEDIA_TYPE;
import static org.androidannotations.rest.spring.helper.RestSpringClasses.RESPONSE_ENTITY;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.rest.spring.annotations.Accept;
import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Part;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.RequiresAuthentication;
import org.androidannotations.rest.spring.annotations.RequiresCookie;
import org.androidannotations.rest.spring.annotations.RequiresCookieInUrl;
import org.androidannotations.rest.spring.annotations.RequiresHeader;
import org.androidannotations.rest.spring.annotations.SetsCookie;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JVar;

public class RestAnnotationHelper extends TargetAnnotationHelper {

	private APTCodeModelHelper codeModelHelper;

	public RestAnnotationHelper(AndroidAnnotationsEnvironment environment, String annotationName) {
		super(environment, annotationName);
		codeModelHelper = new APTCodeModelHelper(environment);
	}

	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	public Set<String> extractUrlVariableNames(ExecutableElement element) {
		String uriTemplate = extractAnnotationValueParameter(element);

		return extractUrlVariableNames(uriTemplate);
	}


	public Set<String> extractUrlVariableNames(String uriTemplate) {

		Set<String> variableNames = new HashSet<>();

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
		Map<String, String> urlNameToElementName = new HashMap<String, String>();
		for (VariableElement variableElement : element.getParameters()) {
			if (variableElement.getAnnotation(Path.class) != null) {
				urlNameToElementName.put(getUrlVariableCorrespondingTo(variableElement), variableElement.getSimpleName().toString());
			}
		}

		Set<String> urlVariables = extractUrlVariableNames(element);

		// cookies in url?
		String[] cookiesToUrl = requiredUrlCookies(element);
		if (cookiesToUrl != null) {
			for (String cookie : cookiesToUrl) {
				urlVariables.add(cookie);
			}
		}

		AbstractJClass hashMapClass = getEnvironment().getClasses().HASH_MAP.narrow(String.class, Object.class);
		if (!urlVariables.isEmpty()) {
			JVar hashMapVar = methodBody.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));
			for (String urlVariable : urlVariables) {
				String elementName = urlNameToElementName.get(urlVariable);
				if (elementName != null) {
					JVar methodParam = methodParams.get(elementName);
					methodBody.invoke(hashMapVar, "put").arg(urlVariable).arg(methodParam);
					methodParams.remove(elementName);
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

	public boolean multipartHeaderRequired(ExecutableElement executableElement) {
		for (VariableElement parameter : executableElement.getParameters()) {
			if (parameter.getAnnotation(Part.class) != null) {
				return true;
			}
		}
		return false;
	}

	public String[] requiredHeaders(ExecutableElement executableElement) {
		RequiresHeader requiresHeaderAnnotation = executableElement.getAnnotation(RequiresHeader.class);
		if (requiresHeaderAnnotation == null) {
			requiresHeaderAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresHeader.class);
		}
		if (requiresHeaderAnnotation != null) {
			return requiresHeaderAnnotation.value();
		} else {
			return null;
		}
	}

	private Map<String, String> getHeadersFromAnnotations(ExecutableElement executableElement) {
		Headers headers = executableElement.getAnnotation(Headers.class);
		Map<String, String> headerMap = new HashMap<>();
		if (headers != null) {
			Header[] headerList = headers.value();

			for (Header header : headerList) {
				headerMap.put(header.name(), header.value());
			}
		}

		Header header = executableElement.getAnnotation(Header.class);
		if (header != null) {
			headerMap.put(header.name(), header.value());
		}

		return headerMap;
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

	public String[] requiredUrlCookies(ExecutableElement executableElement) {
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

		boolean requiresMultipartHeader = multipartHeaderRequired(executableElement);

		Map<String, String> headersFromAnnotations = getHeadersFromAnnotations(executableElement);

		if (hasMediaTypeDefined || requiresCookies || requiresHeaders || requiresAuth || requiresMultipartHeader || !headersFromAnnotations.isEmpty()) {
			// we need the headers
			httpHeadersVar = body.decl(getEnvironment().getJClass(HTTP_HEADERS), "httpHeaders", JExpr._new(getEnvironment().getJClass(HTTP_HEADERS)));
		}

		if (hasMediaTypeDefined) {
			AbstractJClass collectionsClass = getEnvironment().getJClass(CanonicalNameConstants.COLLECTIONS);
			AbstractJClass mediaTypeClass = getEnvironment().getJClass(MEDIA_TYPE);

			JInvocation mediaTypeListParam = collectionsClass.staticInvoke("singletonList").arg(mediaTypeClass.staticInvoke("parseMediaType").arg(mediaType));
			body.add(JExpr.invoke(httpHeadersVar, "setAccept").arg(mediaTypeListParam));
		}

		// Set pre-defined headers here so that they can be overridden by any
		// runtime calls

		if (headersFromAnnotations != null) {
			for (Map.Entry<String, String> header : headersFromAnnotations.entrySet()) {
				body.add(JExpr.invoke(httpHeadersVar, "set").arg(header.getKey()).arg(header.getValue()));
			}
		}

		if (requiresCookies) {
			AbstractJClass stringBuilderClass = getEnvironment().getClasses().STRING_BUILDER;
			JVar cookiesValueVar = body.decl(stringBuilderClass, "cookiesValue", JExpr._new(stringBuilderClass));
			for (String cookie : cookies) {
				JInvocation cookieValue = JExpr.invoke(holder.getAvailableCookiesField(), "get").arg(cookie);
				JInvocation cookieFormatted = getEnvironment().getClasses().STRING.staticInvoke("format").arg(String.format("%s=%%s;", cookie)).arg(cookieValue);
				JInvocation appendCookie = JExpr.invoke(cookiesValueVar, "append").arg(cookieFormatted);
				body.add(appendCookie);
			}

			JInvocation cookiesToString = cookiesValueVar.invoke("toString");
			body.add(JExpr.invoke(httpHeadersVar, "set").arg("Cookie").arg(cookiesToString));
		}

		if (requiresMultipartHeader) {
			body.add(JExpr.invoke(httpHeadersVar, "set").arg(JExpr.lit("Content-Type")).arg(getEnvironment().getJClass(MEDIA_TYPE).staticRef("MULTIPART_FORM_DATA_VALUE")));
		}

		if (requiresHeaders) {
			for (String header : headers) {
				JBlock block = null;
				if (headersFromAnnotations.containsKey(header)) {
					block = body._if(JExpr.invoke(holder.getAvailableHeadersField(), "containsKey").arg(header))._then();
				} else {
					block = body;
				}

				JInvocation headerValue = JExpr.invoke(holder.getAvailableHeadersField(), "get").arg(header);
				block.add(JExpr.invoke(httpHeadersVar, "set").arg(header).arg(headerValue));
			}

		}

		if (requiresAuth) {
			// attach auth
			body.add(httpHeadersVar.invoke("setAuthorization").arg(holder.getAuthenticationField()));
		}

		return httpHeadersVar;
	}

	public JVar getEntitySentToServer(ExecutableElement element, SortedMap<String, JVar> params) {
		for (VariableElement parameter : element.getParameters()) {
			if (parameter.getAnnotation(Body.class) != null) {
				return params.get(parameter.getSimpleName().toString());
			}
		}
		return null;
	}

	public String getUrlVariableCorrespondingTo(VariableElement parameter) {
		return extractParameter(parameter, Path.class);
	}

	public IJExpression declareHttpEntity(JBlock body, JVar entitySentToServer, JVar httpHeaders) {
		AbstractJType entityType = getEnvironment().getJClass(Object.class);

		if (entitySentToServer != null) {
			entityType = entitySentToServer.type();
			if (entityType.isPrimitive()) {
				// Don't narrow primitive types...
				entityType = entityType.boxify();
			}
		}

		AbstractJClass httpEntity = getEnvironment().getJClass(HTTP_ENTITY);
		AbstractJClass narrowedHttpEntity = httpEntity.narrow(entityType);
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

	public IJExpression getResponseClass(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		IJExpression responseClassExpr = nullCastedToNarrowedClass(holder);
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() != TypeKind.VOID) {
			if (getElementUtils().getTypeElement(RestSpringClasses.PARAMETERIZED_TYPE_REFERENCE) != null && !returnType.toString().startsWith(RestSpringClasses.RESPONSE_ENTITY)
					&& checkIfParameterizedTypeReferenceShouldBeUsed(returnType)) {
				return createParameterizedTypeReferenceAnonymousSubclassInstance(returnType);
			}

			AbstractJClass responseClass = retrieveResponseClass(returnType, holder);
			if (responseClass != null) {
				responseClassExpr = responseClass.dotclass();
			}
		}
		return responseClassExpr;
	}

	private boolean checkIfParameterizedTypeReferenceShouldBeUsed(TypeMirror returnType) {
		switch (returnType.getKind()) {
		case DECLARED:
			return !((DeclaredType) returnType).getTypeArguments().isEmpty();

		case ARRAY:
			ArrayType arrayType = (ArrayType) returnType;
			TypeMirror componentType = arrayType.getComponentType();
			return checkIfParameterizedTypeReferenceShouldBeUsed(componentType);
		}
		return false;
	}

	public IJExpression createParameterizedTypeReferenceAnonymousSubclassInstance(TypeMirror returnType) {
		AbstractJClass narrowedTypeReference = getEnvironment().getJClass(RestSpringClasses.PARAMETERIZED_TYPE_REFERENCE).narrow(codeModelHelper.typeMirrorToJClass(returnType));
		JDefinedClass anonymousClass = getEnvironment().getCodeModel().anonymousClass(narrowedTypeReference);
		return JExpr._new(anonymousClass);
	}

	public AbstractJClass retrieveResponseClass(TypeMirror returnType, RestHolder holder) {
		String returnTypeString = returnType.toString();

		AbstractJClass responseClass;

		if (returnTypeString.startsWith(RESPONSE_ENTITY)) {
			DeclaredType declaredReturnType = (DeclaredType) returnType;
			if (declaredReturnType.getTypeArguments().size() > 0) {
				responseClass = resolveResponseClass(declaredReturnType.getTypeArguments().get(0), holder, false);
			} else {
				responseClass = getEnvironment().getJClass(RESPONSE_ENTITY);
			}
		} else {
			responseClass = resolveResponseClass(returnType, holder, true);
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
	private AbstractJClass resolveResponseClass(TypeMirror expectedType, RestHolder holder, boolean useTypeReference) {
		// is a class or an interface
		if (expectedType.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredType = (DeclaredType) expectedType;

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			// is NOT a generics, return directly
			if (typeArguments.isEmpty()) {
				return codeModelHelper.typeMirrorToJClass(declaredType);
			}

			// is a generics, must generate a new super class
			TypeElement declaredElement = (TypeElement) declaredType.asElement();

			if (useTypeReference && getElementUtils().getTypeElement(RestSpringClasses.PARAMETERIZED_TYPE_REFERENCE) != null) {
				return codeModelHelper.typeMirrorToJClass(declaredType);
			}

			AbstractJClass baseClass = codeModelHelper.typeMirrorToJClass(declaredType).erasure();
			AbstractJClass decoratedExpectedClass = retrieveDecoratedResponseClass(declaredType, declaredElement, holder);
			if (decoratedExpectedClass == null) {
				decoratedExpectedClass = baseClass;
			}
			return decoratedExpectedClass;
		} else if (expectedType.getKind() == TypeKind.ARRAY) {
			ArrayType arrayType = (ArrayType) expectedType;
			TypeMirror componentType = arrayType.getComponentType();
			return resolveResponseClass(componentType, holder, false).array();
		}

		// is not a class nor an interface, return directly
		return codeModelHelper.typeMirrorToJClass(expectedType);
	}

	/**
	 * Recursive method used to find if one of the grand-parent of the
	 * <code>enclosingJClass</code> is {@link java.util.Map Map}, {@link Set} or
	 * {@link java.util.Collection Collection}.
	 */
	private AbstractJClass retrieveDecoratedResponseClass(DeclaredType declaredType, TypeElement typeElement, RestHolder holder) {
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
			AbstractJClass decoratedSuperClass = getEnvironment().getJClass(decoratedClassName);
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
				AbstractJClass narrowJClass = codeModelHelper.typeMirrorToJClass(actualTypeArgument);
				decoratedSuperClass = decoratedSuperClass.narrow(narrowJClass);
				decoratedClassNameSuffix += plainName(narrowJClass);
			}

			String decoratedFinalClassName = classTypeBaseName + "_" + decoratedClassNameSuffix;
			decoratedFinalClassName = decoratedFinalClassName.replaceAll("\\[\\]", "s");
			String packageName = holder.getGeneratedClass()._package().name();
			decoratedFinalClassName = packageName + "." + decoratedFinalClassName;
			JDefinedClass decoratedJClass = getEnvironment().getDefinedClass(decoratedFinalClassName);
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

	protected String plainName(AbstractJClass jClass) {
		String plainName = jClass.erasure().name();
		List<? extends AbstractJClass> typeParameters = jClass.getTypeParameters();
		if (typeParameters.size() > 0) {
			plainName += "_";
			for (AbstractJClass typeParameter : typeParameters) {
				plainName += plainName(typeParameter);
			}
		}
		return plainName;
	}

	public IJExpression nullCastedToNarrowedClass(RestHolder holder) {
		return JExpr.cast(getEnvironment().getJClass(Class.class).narrow(getEnvironment().getJClass(Void.class)), JExpr._null());
	}

	/**
	 * Returns the post parameter name to method parameter name mapping, or null
	 * if duplicate names found.
	 */
	public Map<String, String> extractFieldAndPartParameters(ExecutableElement element) {
		Map<String, String> postParameterNameToElementName = new HashMap<String, String>();

		for (VariableElement parameter : element.getParameters()) {
			String parameterName = null;

			if (parameter.getAnnotation(Field.class) != null) {
				parameterName = extractParameter(parameter, Field.class);
			} else if (parameter.getAnnotation(Part.class) != null) {
				parameterName = extractParameter(parameter, Part.class);
			}

			if (parameterName != null) {
				if (postParameterNameToElementName.containsKey(parameterName)) {
					return null;
				}

				postParameterNameToElementName.put(parameterName, parameter.getSimpleName().toString());
			}
		}
		return postParameterNameToElementName;
	}

	private String extractParameter(VariableElement parameter, Class<? extends Annotation> clazz) {
		String value = extractAnnotationParameter(parameter, clazz.getCanonicalName(), "value");

		return !value.equals("") ? value : parameter.getSimpleName().toString();
	}

	public boolean hasRestApiMethodParameterAnnotation(VariableElement variableElement) {
		return hasOneOfClassAnnotations(variableElement, Arrays.asList(Field.class, Part.class, Body.class, Path.class));
	}
}
