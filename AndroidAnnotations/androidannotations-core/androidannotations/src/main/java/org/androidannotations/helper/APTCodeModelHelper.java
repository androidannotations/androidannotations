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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import org.androidannotations.annotations.EBean;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.GeneratedClassHolder;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JSuperWildcard;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class APTCodeModelHelper {

	public JClass typeMirrorToJClass(TypeMirror type, GeneratedClassHolder holder) {
		return typeMirrorToJClass(type, holder, Collections.<String, TypeMirror> emptyMap());
	}

	private JClass typeMirrorToJClass(TypeMirror type, GeneratedClassHolder holder, Map<String, TypeMirror> substitute) {
		if (type instanceof DeclaredType) {
			return typeMirrorToJClass((DeclaredType) type, holder, substitute);
		} else if (type instanceof WildcardType) {
			return typeMirrorToJClass((WildcardType) type, holder, substitute);
		} else if (type instanceof ArrayType) {
			return typeMirrorToJClass((ArrayType) type, holder, substitute);
		} else {
			TypeMirror substituted = substitute.get(type.toString());
			if (substituted != null && type != substituted) {
				return typeMirrorToJClass(substituted, holder, substitute);
			}
			return holder.refClass(type.toString());
		}
	}

	private JClass typeMirrorToJClass(DeclaredType declaredType, GeneratedClassHolder holder, Map<String, TypeMirror> substitute) {
		String declaredTypeName = declaredType.asElement().toString();

		JClass declaredClass = holder.refClass(declaredTypeName);

		List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

		List<JClass> typeArgumentJClasses = new ArrayList<>();
		for (TypeMirror typeArgument : typeArguments) {
			typeArgumentJClasses.add(typeMirrorToJClass(typeArgument, holder, substitute));
		}
		if (typeArgumentJClasses.size() > 0) {
			declaredClass = declaredClass.narrow(typeArgumentJClasses);
		}

		return declaredClass;
	}

	private JClass typeMirrorToJClass(WildcardType wildcardType, GeneratedClassHolder holder, Map<String, TypeMirror> substitute) {
		TypeMirror bound = wildcardType.getExtendsBound();
		if (bound == null) {
			bound = wildcardType.getSuperBound();
			if (bound == null) {
				return holder.classes().OBJECT.wildcard();
			}
			return new JSuperWildcard(typeMirrorToJClass(bound, holder, substitute));
		}

		TypeMirror extendsBound = wildcardType.getExtendsBound();

		if (extendsBound == null) {
			return holder.classes().OBJECT.wildcard();
		} else {
			return typeMirrorToJClass(extendsBound, holder, substitute).wildcard();
		}
	}

	private JClass typeMirrorToJClass(ArrayType arrayType, GeneratedClassHolder holder, Map<String, TypeMirror> substitute) {
		JClass refClass = typeMirrorToJClass(arrayType.getComponentType(), holder, substitute);
		return refClass.array();
	}

	private Map<String, TypeMirror> getActualTypes(Types typeUtils, DeclaredType baseClass, TypeMirror annotatedClass) {
		List<TypeMirror> superTypes = new ArrayList<>();
		superTypes.add(annotatedClass);
		while (!superTypes.isEmpty()) {
			TypeMirror x = superTypes.remove(0);
			if (typeUtils.isSameType(typeUtils.erasure(x), typeUtils.erasure(baseClass))) {
				DeclaredType type = (DeclaredType) x;
				Map<String, TypeMirror> actualTypes = new HashMap<>();
				for (int i = 0; i < type.getTypeArguments().size(); i++) {
					TypeMirror actualArg = type.getTypeArguments().get(i);
					TypeMirror formalArg = baseClass.getTypeArguments().get(i);
					if (!typeUtils.isSameType(actualArg, formalArg)) {
						actualTypes.put(formalArg.toString(), actualArg);
					}
				}
				return actualTypes;
			}
			superTypes.addAll(typeUtils.directSupertypes(x));
		}
		return Collections.emptyMap();
	}

	public JClass typeBoundsToJClass(GeneratedClassHolder holder, List<? extends TypeMirror> bounds) {
		return typeBoundsToJClass(holder, bounds, Collections.<String, TypeMirror> emptyMap());
	}

	private JClass typeBoundsToJClass(GeneratedClassHolder holder, List<? extends TypeMirror> bounds, Map<String, TypeMirror> actualTypes) {
		if (bounds.isEmpty()) {
			return holder.classes().OBJECT;
		} else {
			// TODO resolve <T extends A&B> bounds
			return typeMirrorToJClass(bounds.get(0), holder, actualTypes);
		}
	}

	public JMethod overrideAnnotatedMethod(ExecutableElement executableElement, GeneratedClassHolder holder) {
		TypeMirror annotatedClass = holder.getAnnotatedElement().asType();
		DeclaredType baseClass = (DeclaredType) executableElement.getEnclosingElement().asType();

		Types typeUtils = holder.processingEnvironment().getTypeUtils();

		Map<String, TypeMirror> actualTypes = getActualTypes(typeUtils, baseClass, annotatedClass);
		Map<String, JClass> methodTypes = new LinkedHashMap<>();

		for (TypeParameterElement typeParameter : executableElement.getTypeParameters()) {
			List<? extends TypeMirror> bounds = typeParameter.getBounds();
			JClass jClassBounds = typeBoundsToJClass(holder, bounds, actualTypes);
			methodTypes.put(typeParameter.toString(), jClassBounds);
		}

		actualTypes.keySet().removeAll(methodTypes.keySet());

		JMethod existingMethod = findAlreadyGeneratedMethod(executableElement, holder);
		if (existingMethod != null) {
			return existingMethod;
		}

		String methodName = executableElement.getSimpleName().toString();
		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder, actualTypes);
		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, returnType, methodName);
		addNonAAAnotations(method, executableElement.getAnnotationMirrors(), holder);

		if (!hasAnnotation(method, Override.class)) {
			method.annotate(Override.class);
		}

		for (Map.Entry<String, JClass> typeDeclaration : methodTypes.entrySet()) {
			method.generify(typeDeclaration.getKey(), typeDeclaration.getValue());
		}

		for (VariableElement parameter : executableElement.getParameters()) {
			addParamToMethod(method, parameter, JMod.FINAL, holder, actualTypes);
		}

		for (TypeMirror superThrownType : executableElement.getThrownTypes()) {
			JClass thrownType = typeMirrorToJClass(superThrownType, holder, actualTypes);
			method._throws(thrownType);
		}

		callSuperMethod(method, holder, method.body());

		return method;
	}

	public void generifyStaticHelper(GeneratedClassHolder holder, JMethod staticHelper, TypeElement annotatedClass) {
		for (TypeParameterElement param : annotatedClass.getTypeParameters()) {
			JClass bounds = typeBoundsToJClass(holder, param.getBounds());
			staticHelper.generify(param.getSimpleName().toString(), bounds);
		}
	}

	public JClass narrowGeneratedClass(JClass generatedClass, TypeMirror fromTypeArguments, GeneratedClassHolder holder) {
		DeclaredType type = (DeclaredType) fromTypeArguments;

		for (TypeMirror param : type.getTypeArguments()) {
			JClass paramClass = typeMirrorToJClass(param, holder);
			generatedClass = generatedClass.narrow(paramClass);
		}
		return generatedClass;
	}

	private JMethod findAlreadyGeneratedMethod(ExecutableElement executableElement, GeneratedClassHolder holder) {
		JDefinedClass definedClass = holder.getGeneratedClass();
		String methodName = executableElement.getSimpleName().toString();
		List<? extends VariableElement> parameters = executableElement.getParameters();
		// CHECKSTYLE:OFF
		// TODO: refactor the nasty label jump
		method: for (JMethod method : definedClass.methods()) {
			if (method.name().equals(methodName) && method.params().size() == parameters.size()) {
				int i = 0;
				for (JVar param : method.params()) {
					String searchedParamType = typeMirrorToJClass(parameters.get(i).asType(), holder).name();
					if (!param.type().name().equals(searchedParamType)) {
						continue method;
					}
					i++;
				}
				return method;
			}
		}
		// CHECKSTYLE:ON
		return null;
	}

	private void addParamToMethod(JMethod method, VariableElement parameter, int mod, GeneratedClassHolder holder, Map<String, TypeMirror> actualTypes) {
		String parameterName = parameter.getSimpleName().toString();
		JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder, actualTypes);
		JVar param = method.param(mod, parameterClass, parameterName);
		addNonAAAnotations(param, parameter.getAnnotationMirrors(), holder);
	}

	public void addNonAAAnotations(JAnnotatable annotatable, List<? extends AnnotationMirror> annotationMirrors, GeneratedClassHolder holder) {
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationMirror.getAnnotationType().asElement().getAnnotation(Inherited.class) == null) {
				JClass annotationClass = typeMirrorToJClass(annotationMirror.getAnnotationType(), holder);
				if (!annotationClass.fullName().startsWith("org.androidannotations")) {
					addAnnotation(annotatable, annotationMirror, holder);
				}
			}
		}
	}

	public void addAnnotation(JAnnotatable annotatable, AnnotationMirror annotationMirror, GeneratedClassHolder holder) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> parameters = annotationMirror.getElementValues();

		if (!hasAnnotation(annotatable, annotationMirror)) {
			JClass annotation = typeMirrorToJClass(annotationMirror.getAnnotationType(), holder);
			JAnnotationUse annotate = annotatable.annotate(annotation);

			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : parameters.entrySet()) {
				param.getValue().accept(new AnnotationParamExtractor(annotate, holder, this), param.getKey().getSimpleName().toString());
			}
		}
	}

	private boolean hasAnnotation(JAnnotatable annotatable, AnnotationMirror annotationMirror) {
		return hasAnnotation(annotatable, annotationMirror.getAnnotationType().toString());
	}

	public boolean hasAnnotation(JAnnotatable annotatable, Class<? extends Annotation> annotationClass) {
		return hasAnnotation(annotatable, annotationClass.getCanonicalName());
	}

	private boolean hasAnnotation(JAnnotatable annotatable, String annotationFQN) {
		for (JAnnotationUse annotation : annotatable.annotations()) {
			if (annotation.getAnnotationClass().fullName().equals(annotationFQN)) {
				return true;
			}
		}
		return false;
	}

	public JInvocation getSuperCall(GeneratedClassHolder holder, JMethod superMethod) {
		JExpression activitySuper = holder.getGeneratedClass().staticRef("super");
		JInvocation superCall = JExpr.invoke(activitySuper, superMethod);

		for (JVar param : superMethod.params()) {
			superCall.arg(param);
		}

		return superCall;
	}

	public void callSuperMethod(JMethod superMethod, GeneratedClassHolder holder, JBlock callBlock) {
		JInvocation superCall = getSuperCall(holder, superMethod);

		JType returnType = superMethod.type();
		if (returnType.fullName().equals("void")) {
			callBlock.add(superCall);
		} else {
			callBlock._return(superCall);
		}
	}

	public JBlock removeBody(JMethod method) {
		JBlock body = method.body();
		try {
			Field bodyField = JMethod.class.getDeclaredField("body");
			bodyField.setAccessible(true);
			bodyField.set(method, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		JBlock clonedBody = new JBlock(false, false);
		copy(body, clonedBody);
		return clonedBody;
	}

	public void copy(JBlock body, JBlock newBody) {
		for (Object statement : body.getContents()) {
			if (statement instanceof JVar) {
				JVar var = (JVar) statement;
				try {
					Field varInitField = JVar.class.getDeclaredField("init");
					varInitField.setAccessible(true);
					JExpression varInit = (JExpression) varInitField.get(var);

					newBody.decl(var.type(), var.name(), varInit);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				newBody.add((JStatement) statement);
			}
		}
	}

	public void replaceSuperCall(JMethod method, JBlock replacement) {
		String superCallStart = "super." + method.name() + "(";

		JBlock oldBody = removeBody(method);
		JBlock newBody = method.body();

		for (Object content : oldBody.getContents()) {
			StringWriter writer = new StringWriter();
			JFormatter formatter = new JFormatter(writer);
			JStatement statement = (JStatement) content;
			statement.state(formatter);
			String statementString = writer.getBuffer().toString();
			if (statementString.startsWith(superCallStart)) {
				newBody.add(replacement);
			} else {
				newBody.add(statement);
			}
		}
	}

	public String getIdStringFromIdFieldRef(JFieldRef idRef) {
		try {
			Field nameField = JFieldRef.class.getDeclaredField("name");
			nameField.setAccessible(true);
			String name = (String) nameField.get(idRef);

			if (name != null) {
				return name;
			}

			Field varField = JFieldRef.class.getDeclaredField("var");
			varField.setAccessible(true);
			JVar var = (JVar) varField.get(idRef);

			if (var != null) {
				return var.name();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		throw new IllegalStateException("Unable to extract target name from JFieldRef");
	}

	public JDefinedClass createDelegatingAnonymousRunnableClass(EComponentHolder holder, JBlock previousBody) {
		JCodeModel codeModel = holder.codeModel();

		JDefinedClass anonymousRunnableClass = codeModel.anonymousClass(Runnable.class);

		JMethod runMethod = anonymousRunnableClass.method(JMod.PUBLIC, codeModel.VOID, "run");
		runMethod.annotate(Override.class);
		runMethod.body().add(previousBody);

		return anonymousRunnableClass;
	}

	/**
	 * Gets all of the methods of the class and includes the methods of any
	 * implemented interfaces.
	 *
	 * @param typeElement
	 * @return full list of methods.
	 */
	public List<ExecutableElement> getMethods(TypeElement typeElement) {
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = new ArrayList<>(ElementFilter.methodsIn(enclosedElements));

		// Add methods of the interfaces. These will be valid as they have gone
		// through the validator.
		for (TypeMirror iface : typeElement.getInterfaces()) {
			DeclaredType dt = (DeclaredType) iface;
			methods.addAll(ElementFilter.methodsIn(dt.asElement().getEnclosedElements()));
		}

		return methods;
	}

	public JMethod implementMethod(GeneratedClassHolder holder, List<ExecutableElement> methods, String methodName, String returnType, String... parameterTypes) {
		return implementMethod(holder, methods, methodName, returnType, false, parameterTypes);
	}

	public JMethod implementMethod(GeneratedClassHolder holder, List<ExecutableElement> methods, String methodName, String returnType, boolean finalParams, String... parameterTypes) {
		// First get the ExecutableElement method object from the util function.
		ExecutableElement method = getMethod(methods, methodName, returnType, parameterTypes);
		JMethod jmethod = null;

		if (method != null) {
			// Get the return type or VOID if none.
			JType jcReturnType = returnType.equals(TypeKind.VOID.toString()) ? holder.codeModel().VOID : holder.refClass(returnType);

			// Create the implementation and annotate it with the Override
			// annotation.
			jmethod = holder.getGeneratedClass().method(JMod.PUBLIC, jcReturnType, method.getSimpleName().toString());
			jmethod.annotate(Override.class);

			// Create the parameters.
			int paramMods = finalParams ? JMod.FINAL : JMod.NONE;
			for (int i = 0; i < method.getParameters().size(); i++) {
				VariableElement param = method.getParameters().get(i);
				jmethod.param(paramMods, holder.refClass(parameterTypes[i]), param.getSimpleName().toString());
			}
		}

		return jmethod;
	}

	private ExecutableElement getMethod(List<ExecutableElement> methods, String methodName, String returnType, String... parameterTypes) {
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();

			// Get the method return type or "VOID" if none.
			String methodReturnType = method.getReturnType().getKind() == TypeKind.VOID ? TypeKind.VOID.toString() : method.getReturnType().toString();

			if (parameters.size() == parameterTypes.length && methodReturnType.equals(returnType)) {
				if (methodName == null || method.getSimpleName().toString().equals(methodName)) {
					// At this point, method name, return type and number of
					// parameters are correct. Now we need to validate the
					// parameter types.
					boolean validMethod = true;

					for (int i = 0; i < parameters.size(); i++) {
						VariableElement param = parameters.get(i);

						if (!param.asType().toString().equals(parameterTypes[i])) {
							// Parameter type does not match, this is not the
							// correct method.
							validMethod = false;
							break;
						}
					}

					if (validMethod) {
						return method;
					}
				}
			}
		}

		return null;
	}

	public JInvocation newBeanOrEBean(GeneratedClassHolder holder, DeclaredType beanType, JVar contextVar) {
		if (beanType.asElement().getAnnotation(EBean.class) != null) {
			String typeQualifiedName = beanType.toString();
			JClass injectedClass = holder.refClass(typeQualifiedName + classSuffix());
			return injectedClass.staticInvoke(EBeanHolder.GET_INSTANCE_METHOD_NAME).arg(contextVar);
		} else {
			return _new(holder.refClass(beanType.toString()));
		}
	}

	public JExpression litObject(Object o) {
		if (o instanceof Integer) {
			return lit((Integer) o);
		} else if (o instanceof Float) {
			return lit((Float) o);
		} else if (o instanceof Long) {
			return lit((Long) o);
		} else if (o instanceof Boolean) {
			return lit((Boolean) o);
		} else {
			return lit((String) o);
		}
	}

	// TODO it would be nice to cache the result map for better performance
	public TypeMirror getActualType(Element element, GeneratedClassHolder holder) {
		Types types = holder.processingEnvironment().getTypeUtils();
		DeclaredType typeMirror = (DeclaredType) element.getEnclosingElement().asType();
		TypeMirror annotatedClass = holder.getAnnotatedElement().asType();

		Map<String, TypeMirror> actualTypes = getActualTypes(types, typeMirror, annotatedClass);

		TypeMirror type = actualTypes.get(element.asType().toString());
		return type == null ? element.asType() : type;
	}

	public void addSuppressWarnings(JAnnotatable generatedElement, String annotationValue) {
		Collection<JAnnotationUse> annotations = generatedElement.annotations();
		for (JAnnotationUse annotationUse : annotations) {
			if (annotationUse.getAnnotationClass().fullName().equals(SuppressWarnings.class.getCanonicalName())) {
				JAnnotationValue value = annotationUse.getAnnotationMembers().values().iterator().next();
				StringWriter code = new StringWriter();
				JFormatter formatter = new JFormatter(code);
				formatter.g(value);
				if (!code.toString().contains(annotationValue)) {
					if (value instanceof JAnnotationArrayMember) {
						((JAnnotationArrayMember) value).param(annotationValue);
					} else {
						String foundValue = code.toString().substring(1, code.toString().length() - 1);
						JAnnotationArrayMember newParamArray = annotationUse.paramArray("value");
						newParamArray.param(foundValue).param(annotationValue);
					}
				}
				return;
			}
		}

		generatedElement.annotate(SuppressWarnings.class).param("value", annotationValue);
	}
}
