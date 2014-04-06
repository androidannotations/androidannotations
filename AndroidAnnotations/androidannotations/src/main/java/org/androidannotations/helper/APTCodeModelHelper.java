/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
			DeclaredType declaredType = (DeclaredType) type;

			String declaredTypeName = declaredType.asElement().toString();

			JClass declaredClass = holder.refClass(declaredTypeName);

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			List<JClass> typeArgumentJClasses = new ArrayList<JClass>();
			for (TypeMirror typeArgument : typeArguments) {
				typeArgumentJClasses.add(typeMirrorToJClass(typeArgument, holder, substitute));
			}
			if (typeArgumentJClasses.size() > 0) {
				declaredClass = declaredClass.narrow(typeArgumentJClasses);
			}

			return declaredClass;
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;

			TypeMirror bound = wildcardType.getExtendsBound();
			if (bound == null) {
				bound = wildcardType.getSuperBound();
				if (bound == null) {
					return holder.classes().OBJECT.wildcard();
				}
				return superWildcard(typeMirrorToJClass(bound, holder, substitute));
			}

			TypeMirror extendsBound = wildcardType.getExtendsBound();

			if (extendsBound == null) {
				return holder.classes().OBJECT.wildcard();
			} else {
				return typeMirrorToJClass(extendsBound, holder, substitute).wildcard();
			}
		} else if (type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) type;

			JClass refClass = typeMirrorToJClass(arrayType.getComponentType(), holder, substitute);

			return refClass.array();
		} else {
			TypeMirror substituted = substitute.get(type.toString());
			if (substituted != null && type != substituted) {
				return typeMirrorToJClass(substituted, holder, substitute);
			}
			return holder.refClass(type.toString());
		}
	}

	private JClass superWildcard(final JClass bound) {
		return new JSuperWildcard(bound);
	}

	public static class Parameter {
		public final String name;
		public final JClass jClass;

		public Parameter(String name, JClass jClass) {
			this.name = name;
			this.jClass = jClass;
		}
	}

	private Map<String, TypeMirror> getActualTypes(Types typeUtils, DeclaredType baseClass, TypeMirror annotatedClass) {
		List<TypeMirror> superTypes = new ArrayList<TypeMirror>();
		superTypes.add(annotatedClass);
		while (!superTypes.isEmpty()) {
			TypeMirror x = superTypes.remove(0);
			if (typeUtils.isSameType(typeUtils.erasure(x), typeUtils.erasure(baseClass))) {
				DeclaredType type = (DeclaredType) x;
				Map<String, TypeMirror> actualTypes = new HashMap<String, TypeMirror>();
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
		Map<String, JClass> methodTypes = new LinkedHashMap<String, JClass>();

		for (TypeParameterElement typeParameter : executableElement.getTypeParameters()) {
			List<? extends TypeMirror> bounds = typeParameter.getBounds();
			JClass jClassBounds = typeBoundsToJClass(holder, bounds, actualTypes);
			methodTypes.put(typeParameter.toString(), jClassBounds);
		}

		actualTypes.keySet().removeAll(methodTypes.keySet());

		String methodName = executableElement.getSimpleName().toString();
		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder, actualTypes);

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (int i = 0; i < executableElement.getParameters().size(); i++) {
			VariableElement parameter = executableElement.getParameters().get(i);

			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder, actualTypes);
			parameters.add(new Parameter(parameterName, parameterClass));
		}

		JMethod existingMethod = findAlreadyGeneratedMethod(holder.getGeneratedClass(), methodName, parameters);

		if (existingMethod != null) {
			return existingMethod;
		}

		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		for (Map.Entry<String, JClass> typeDeclaration : methodTypes.entrySet()) {
			method.generify(typeDeclaration.getKey(), typeDeclaration.getValue());
		}

		for (Parameter parameter : parameters) {
			method.param(JMod.FINAL, parameter.jClass, parameter.name);
		}

		for (TypeMirror superThrownType : executableElement.getThrownTypes()) {
			JClass thrownType = typeMirrorToJClass(superThrownType, holder, actualTypes);
			method._throws(thrownType);
		}

		callSuperMethod(method, holder, method.body());

		return method;
	}

	private JMethod findAlreadyGeneratedMethod(JDefinedClass definedClass, String methodName, List<Parameter> parameters) {
		method: for (JMethod method : definedClass.methods()) {
			if (method.name().equals(methodName) && method.params().size() == parameters.size()) {
				int i = 0;
				for (JVar param : method.params()) {
					String searchedParamType = parameters.get(i).jClass.name();
					if (!param.type().name().equals(searchedParamType)) {
						continue method;
					}
					i++;
				}
				return method;
			}
		}
		return null;
	}

	public void callSuperMethod(JMethod superMethod, GeneratedClassHolder holder, JBlock callBlock) {
		JExpression activitySuper = holder.getGeneratedClass().staticRef("super");
		JInvocation superCall = JExpr.invoke(activitySuper, superMethod);

		for (JVar param : superMethod.params()) {
			superCall.arg(param);
		}

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

	public JDefinedClass createDelegatingAnonymousRunnableClass(EComponentHolder holder, JMethod delegatedMethod) {

		JCodeModel codeModel = holder.codeModel();

		JDefinedClass anonymousRunnableClass;
		JBlock previousMethodBody = removeBody(delegatedMethod);

		anonymousRunnableClass = codeModel.anonymousClass(Runnable.class);

		JMethod runMethod = anonymousRunnableClass.method(JMod.PUBLIC, codeModel.VOID, "run");
		runMethod.annotate(Override.class);

		runMethod.body().add(previousMethodBody);

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
		List<ExecutableElement> methods = new ArrayList<ExecutableElement>(ElementFilter.methodsIn(enclosedElements));

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
			JClass injectedClass = holder.refClass(typeQualifiedName + GENERATION_SUFFIX);
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

}
