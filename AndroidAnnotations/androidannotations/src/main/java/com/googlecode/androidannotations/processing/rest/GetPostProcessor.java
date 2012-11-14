package com.googlecode.androidannotations.processing.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JPackage;

public abstract class GetPostProcessor extends MethodProcessor {

	private void log(String message) {
		System.out.println("GetPostProcessor INFO : " + message);
	}

	private void log(int level, String message) {
		System.out.print("GetPostProcessor INFO : ");
		for (int i = 0; i < level; i++) {
			System.out.print("  ");
		}
		System.out.println(message);
	}

	protected EBeanHolder holder;
	protected JPackage restClientPackage;

	public GetPostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		super(processingEnv, restImplementationsHolder);
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

		log("\n--------- " + returnTypeString.toString() + " ---------");

		if (returnTypeString.startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			DeclaredType declaredReturnType = (DeclaredType) returnType;
			if (declaredReturnType.getTypeArguments().size() > 0) {
				expectedClass = resolveExpectedClass(declaredReturnType.getTypeArguments().get(0));
			} else {
				expectedClass = holder.parseClass(CanonicalNameConstants.RESPONSE_ENTITY);
			}
		} else {
			expectedClass = resolveExpectedClass(returnType);
		}

		log("expectedClass = " + expectedClass.fullName() + " extends " + expectedClass._extends().fullName());

		processorHolder.setExpectedClass(expectedClass);
		processorHolder.setMethodReturnClass(holder.parseClass(returnTypeString));
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
		log("Resolving class for " + expectedType.toString());

		// is a class or an interface
		if (expectedType.getKind() == TypeKind.DECLARED) {
			DeclaredType declaredType = (DeclaredType) expectedType;
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			log(1, "typeArguments = " + typeArguments.toString());

			// is NOT a generics
			if (typeArguments.size() == 0) {
				log(expectedType.toString() + " is NOT a generics");

				return holder.parseClass(expectedType.toString());
			}

			// is a generics
			log(1, expectedType.toString() + " is a generics");

			JClass baseClass = holder.parseClass(declaredType.toString()).erasure();
			JClass decoratedExpectedClass = retrieveDecoratedExpectedClass(declaredType, baseClass);
			return decoratedExpectedClass == null ? baseClass : decoratedExpectedClass;
		} else if (expectedType.getKind() == TypeKind.ARRAY) {
			ArrayType arrayType = (ArrayType) expectedType;
			log(1, arrayType.toString() + " is an array");

			return resolveExpectedClass(arrayType.getComponentType()).array();
		}

		// is not a class nor an interface
		log(1, expectedType.toString() + " is a primitive or an array");
		return holder.parseClass(expectedType.toString());
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
	private JClass retrieveDecoratedExpectedClass(DeclaredType declaredType, JClass currentClass) {
		log(2, "retrieveDecoratedExpectedClass(" + declaredType.toString() + ", " + currentClass.fullName() + ")");

		// Looking for basic java.util interfaces to set a default
		// implementation
		String decoratedClassName = null;
		if (currentClass.fullName().equals(CanonicalNameConstants.MAP)) {
			decoratedClassName = LinkedHashMap.class.getCanonicalName();
		} else if (currentClass.fullName().equals(CanonicalNameConstants.SET)) {
			decoratedClassName = TreeSet.class.getCanonicalName();
		} else if (currentClass.fullName().equals(CanonicalNameConstants.LIST)) {
			decoratedClassName = ArrayList.class.getCanonicalName();
		} else if (currentClass.fullName().equals(CanonicalNameConstants.COLLECTION)) {
			decoratedClassName = ArrayList.class.getCanonicalName();
		}

		if (decoratedClassName != null) {
			log(2, "enclosingJClass " + currentClass.fullName() + " has a known parent : " + decoratedClassName);

			// Configure the super class of the final decorated class
			String decoratedClassNameSuffix = "";
			JClass decoratedSuperClass = holder.parseClass(decoratedClassName);
			for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
				String typeArgumentName = typeArgument.toString();
				if (typeArgument instanceof WildcardType) {
					WildcardType wildcardType = (WildcardType) typeArgument;
					if (wildcardType.getExtendsBound() != null) {
						typeArgumentName = wildcardType.getExtendsBound().toString();
					} else if (wildcardType.getSuperBound() != null) {
						typeArgumentName = wildcardType.getSuperBound().toString();
					} else {
						typeArgumentName = CanonicalNameConstants.OBJECT;
					}
				}
				JClass narrowJClass = holder.parseClass(typeArgumentName);
				decoratedSuperClass = decoratedSuperClass.narrow(narrowJClass);
				decoratedClassNameSuffix += plainName(narrowJClass);
			}

			// TODO: Retrieve or generate decorated classes

			String decoratedFinalClassName = currentClass.name() + "_" + decoratedClassNameSuffix;
			decoratedFinalClassName = decoratedFinalClassName.replaceAll("\\[\\]", "s");
			decoratedFinalClassName = restClientPackage.name() + "." + decoratedFinalClassName;
			JDefinedClass decoratedJClass = holder.definedClass(decoratedFinalClassName);
			decoratedJClass._extends(decoratedSuperClass);

			log(2, "decoratedJClass = " + decoratedJClass.fullName());

			return decoratedJClass;
		}

		// Try to find the superclass and make a recursive call to the this
		// method
		log(2, "Try to find a parent for " + currentClass.toString());
		JClass enclosingSuperJClass = currentClass._extends();
		if (enclosingSuperJClass != null) {
			return retrieveDecoratedExpectedClass(declaredType, enclosingSuperJClass);
		}

		log(2, "Falling back to " + currentClass.toString());

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
