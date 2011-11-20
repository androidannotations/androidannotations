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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
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
	
	public JMethod overrideAnnotatedMethod(ExecutableElement executableElement, EBeanHolder holder) {
		String methodName = executableElement.getSimpleName().toString();
		
		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder);
		
		JMethod method = holder.eBean.method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		List<JVar> parameters = new ArrayList<JVar>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			JVar param = method.param(JMod.FINAL, parameterClass, parameterName);
			parameters.add(param);
		}
		
		for (TypeMirror superThrownType : executableElement.getThrownTypes()) {
			JClass thrownType = typeMirrorToJClass(superThrownType, holder);
			method._throws(thrownType);
		}
		
		return method;
	}
	
	public void callSuperMethod(JMethod superMethod, JCodeModel codeModel, EBeanHolder holder, JBlock callBlock) {
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

}
