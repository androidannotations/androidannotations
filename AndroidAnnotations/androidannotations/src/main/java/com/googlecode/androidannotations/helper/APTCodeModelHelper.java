/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

import static com.sun.codemodel.JExpr.cast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class APTCodeModelHelper {

	public JClass typeMirrorToJClass(TypeMirror type, EBeanHolder holder) {

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
	
	public JMethod overrideAnnotatedMethod(ExecutableElement executableElement, EBeanHolder holder) {

		String methodName = executableElement.getSimpleName().toString();

		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder);

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			parameters.add(new Parameter(parameterName, parameterClass));
		}
		
		JMethod existingMethod = findAlreadyGeneratedMethod(holder.eBean, methodName, parameters);
		
		if (existingMethod != null) {
			return existingMethod;
		}
		
		JMethod method = holder.eBean.method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		List<JVar> methodParameters = new ArrayList<JVar>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			JVar param = method.param(JMod.FINAL, parameterClass, parameterName);
			methodParameters.add(param);
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

	public void callSuperMethod(JMethod superMethod, EBeanHolder holder, JBlock callBlock) {
		JExpression activitySuper = holder.eBean.staticRef("super");
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
		
		for(Object statement : body.getContents()) {
			clonedBody.add((JStatement) statement);
		}
		
		return clonedBody;
	}
	
	public JDefinedClass createDelegatingAnonymousRunnableClass(JCodeModel codeModel, EBeanHolder holder, JMethod delegatedMethod) {
		JDefinedClass anonymousRunnableClass;
		JBlock previousMethodBody = removeBody(delegatedMethod);

		anonymousRunnableClass = codeModel.anonymousClass(Runnable.class);

		JMethod runMethod = anonymousRunnableClass.method(JMod.PUBLIC, codeModel.VOID, "run");
		runMethod.annotate(Override.class);

		JBlock runMethodBody = runMethod.body();
		JTryBlock runTry = runMethodBody._try();

		runTry.body().add(previousMethodBody);

		JCatchBlock runCatch = runTry._catch(holder.refClass(RuntimeException.class));
		JVar exceptionParam = runCatch.param("e");

		JClass logClass = holder.refClass("android.util.Log");

		JInvocation errorInvoke = logClass.staticInvoke("e");

		errorInvoke.arg(holder.eBean.name());
		errorInvoke.arg("A runtime exception was thrown while executing code in a runnable");
		errorInvoke.arg(exceptionParam);

		runCatch.body().add(errorInvoke);
		return anonymousRunnableClass;
	}
	
	public JVar castContextToActivity(EBeanHolder holder, JBlock ifActivityBody) {
		JClass activityClass = holder.refClass("android.app.Activity");
		return ifActivityBody.decl(activityClass, "activity", cast(activityClass, holder.contextRef));
	}

	public JBlock ifContextInstanceOfActivity(EBeanHolder holder, JBlock methodBody) {
		JClass activityClass = holder.refClass("android.app.Activity");
		return methodBody._if(holder.contextRef._instanceof(activityClass))._then();
	}

}
