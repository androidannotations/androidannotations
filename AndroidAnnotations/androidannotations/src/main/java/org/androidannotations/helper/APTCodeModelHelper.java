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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.SERIALIZABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.GeneratedClassHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

public class APTCodeModelHelper {

	public JClass typeMirrorToJClass(TypeMirror type, GeneratedClassHolder holder) {

		if (type instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) type;

			String declaredTypeName = declaredType.asElement().toString();

			JClass declaredClass = holder.refClass(declaredTypeName);

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			List<JClass> typeArgumentJClasses = new ArrayList<JClass>();
			for (TypeMirror typeArgument : typeArguments) {
				typeArgumentJClasses.add(typeMirrorToJClass(typeArgument, holder));
			}
			if (typeArgumentJClasses.size() > 0) {
				declaredClass = declaredClass.narrow(typeArgumentJClasses);
			}

			return declaredClass;
		} else if (type instanceof WildcardType) {
			// TODO : At his time (01/2013), it is not possible to handle the
			// super bound because code model does not offer a way to model
			// statement like " ? super X"
			// (see http://java.net/jira/browse/CODEMODEL-11)
			WildcardType wildcardType = (WildcardType) type;

			TypeMirror extendsBound = wildcardType.getExtendsBound();

			return typeMirrorToJClass(extendsBound, holder).wildcard();
		} else if (type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) type;

			JClass refClass = typeMirrorToJClass(arrayType.getComponentType(), holder);

			return refClass.array();
		} else {
			return holder.refClass(type.toString());
		}
	}

	public static class Parameter {
		public final String name;
		public final JClass jClass;

		public Parameter(String name, JClass jClass) {
			this.name = name;
			this.jClass = jClass;
		}
	}

	public JMethod overrideAnnotatedMethod(ExecutableElement executableElement, GeneratedClassHolder holder) {

		String methodName = executableElement.getSimpleName().toString();

		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder);

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			parameters.add(new Parameter(parameterName, parameterClass));
		}

		JMethod existingMethod = findAlreadyGeneratedMethod(holder.getGeneratedClass(), methodName, parameters);

		if (existingMethod != null) {
			return existingMethod;
		}

		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			method.param(JMod.FINAL, parameterClass, parameterName);
		}

		for (TypeMirror superThrownType : executableElement.getThrownTypes()) {
			JClass thrownType = typeMirrorToJClass(superThrownType, holder);
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

		for (Object statement : body.getContents()) {
			clonedBody.add((JStatement) statement);
		}

		return clonedBody;
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
			for (int i = 0; i < method.getParameters().size(); i++) {
				VariableElement param = method.getParameters().get(i);
				jmethod.param(holder.refClass(parameterTypes[i]), param.getSimpleName().toString());
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

}
