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
package org.androidannotations.processing.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JPackage;

public abstract class GetPostProcessor extends MethodProcessor {

	protected EBeanHolder holder;

	/**
	 * Will be use to generate specific classes
	 */
	protected JPackage restClientPackage;

	protected APTCodeModelHelper helper;

	public GetPostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		super(processingEnv, restImplementationsHolder);
		helper = new APTCodeModelHelper();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		this.holder = holder;
		restClientPackage = restImplementationsHolder.getEnclosingHolder(element).restImplementationClass._package();

		String urlSuffix = retrieveUrlSuffix(element);

		ExecutableElement executableElement = (ExecutableElement) element;
		MethodProcessorHolder processorHolder = new MethodProcessorHolder(holder, executableElement, urlSuffix, null, null, codeModel);

		// Retrieve return type
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() != TypeKind.VOID) {
			retrieveReturnAndExpectedClasses(returnType, processorHolder);
		}

		generateRestTemplateCallBlock(processorHolder);
	}

	public abstract String retrieveUrlSuffix(Element element);

	/**
	 * Retrieve the expected and method return classes to use in generated code.
	 * <p>
	 * If the annotated method return a <b>ResponseEntity&lt;T&gt;</b> then :
	 * 
	 * <pre>
	 * expectedClass = T.class, methodReturnClass = ResponseEntity&lt;T&gt;
	 * </pre>
	 * 
	 * 
	 * @param returnType
	 * @param processorHolder
	 */
	public void retrieveReturnAndExpectedClasses(TypeMirror returnType, MethodProcessorHolder processorHolder) {
		String returnTypeString = returnType.toString();

		JClass expectedClass = null;
		JClass returnClass = helper.typeMirrorToJClass(returnType, holder);

		if (returnTypeString.startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			DeclaredType declaredReturnType = (DeclaredType) returnType;
			if (declaredReturnType.getTypeArguments().size() > 0) {
				expectedClass = resolveExpectedClass(declaredReturnType.getTypeArguments().get(0));
			} else {
				expectedClass = holder.refClass(CanonicalNameConstants.RESPONSE_ENTITY);
			}
		} else {
			expectedClass = resolveExpectedClass(returnType);
		}

		processorHolder.setExpectedClass(expectedClass);
		processorHolder.setMethodReturnClass(returnClass);
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
	 * @param expectedType
	 */
	private JClass resolveExpectedClass(TypeMirror expectedType) {
		// is a class or an interface
		if (expectedType.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredType = (DeclaredType) expectedType;

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			// is NOT a generics, return directly
			if (typeArguments.isEmpty()) {
				return helper.typeMirrorToJClass(declaredType, holder);
			}

			// is a generics, must generate a new super class
			TypeElement declaredElement = (TypeElement) declaredType.asElement();

			JClass baseClass = helper.typeMirrorToJClass(declaredType, holder).erasure();
			JClass decoratedExpectedClass = retrieveDecoratedExpectedClass(declaredType, declaredElement);
			if (decoratedExpectedClass == null) {
				decoratedExpectedClass = baseClass;
			}
			return decoratedExpectedClass;
		} else if (expectedType.getKind() == TypeKind.ARRAY) {
			ArrayType arrayType = (ArrayType) expectedType;
			return resolveExpectedClass(arrayType.getComponentType()).array();
		}

		// is not a class nor an interface, return directly
		return helper.typeMirrorToJClass(expectedType, holder);
	}

	/**
	 * Recursive method used to find if one of the grand-parent of the
	 * <code>enclosingJClass</code> is {@link Map}, {@link Set} or
	 * {@link Collection}.
	 * 
	 * @param declaredType
	 * @param currentClass
	 * @return
	 */
	private JClass retrieveDecoratedExpectedClass(DeclaredType declaredType, TypeElement typeElement) {
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
				JClass narrowJClass = helper.typeMirrorToJClass(typeArgument, holder);
				decoratedSuperClass = decoratedSuperClass.narrow(narrowJClass);
				decoratedClassNameSuffix += plainName(narrowJClass);
			}

			String decoratedFinalClassName = classTypeBaseName + "_" + decoratedClassNameSuffix;
			decoratedFinalClassName = decoratedFinalClassName.replaceAll("\\[\\]", "s");
			decoratedFinalClassName = restClientPackage.name() + "." + decoratedFinalClassName;
			JDefinedClass decoratedJClass = holder.definedClass(decoratedFinalClassName);
			decoratedJClass._extends(decoratedSuperClass);

			return decoratedJClass;
		}

		// Try to find the superclass and make a recursive call to the this
		// method
		TypeMirror enclosingSuperJClass = typeElement.getSuperclass();
		if (enclosingSuperJClass != null && enclosingSuperJClass.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredEnclosingSuperJClass = (DeclaredType) enclosingSuperJClass;
			return retrieveDecoratedExpectedClass(declaredType, (TypeElement) declaredEnclosingSuperJClass.asElement());
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

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(generateHttpEntityVar(methodHolder));
	}

	@Override
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass expectedClass = methodHolder.getExpectedClass();

		if (expectedClass != null) {
			return restCall.arg(expectedClass.dotclass());
		} else {
			return restCall.arg(JExpr._null());
		}
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass generatedReturnType = methodHolder.getMethodReturnClass();
		if (generatedReturnType == null) {
			return restCall;
		}

		if (!generatedReturnType.fullName().startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			restCall = JExpr.invoke(restCall, "getBody");
		}

		return restCall;
	}

}
