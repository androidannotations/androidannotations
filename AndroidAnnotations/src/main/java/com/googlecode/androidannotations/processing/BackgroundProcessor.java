/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.generation.BackgroundInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class BackgroundProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Background.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		String className = metaActivity.getClassSimpleName();
		List<Instruction> memberInstructions = metaActivity.getMemberInstructions();

		List<String> methodArguments = new ArrayList<String>();
		List<String> methodParameters = new ArrayList<String>();

		ExecutableElement executableElement = (ExecutableElement) element;

		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			String parameterType = parameter.asType().toString();
			methodArguments.add(parameterType + " " + parameterName);
			methodParameters.add(parameterName);
		}

		Instruction instruction = new BackgroundInstruction(className, methodName, methodArguments, methodParameters);
		memberInstructions.add(instruction);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, Map<Element, ActivityHolder> activityHolders) throws JClassAlreadyExistsException {

		// Reproduce BackgroundInstruction

		Element enclosingElement = element.getEnclosingElement();

		ActivityHolder holder = activityHolders.get(enclosingElement);

		// Method
		String backgroundMethodName = element.getSimpleName().toString();
		JMethod backgroundMethod = holder.activity.method(JMod.PUBLIC, codeModel.VOID, backgroundMethodName);
		backgroundMethod.annotate(Override.class);

		// Method parameters
		List<JVar> parameters = new ArrayList<JVar>();
		ExecutableElement executableElement = (ExecutableElement) element;
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = codeModel.ref(parameter.asType().toString());
			JVar param = backgroundMethod.param(parameterClass, parameterName);
			parameters.add(param);
		}

		/*
		 * Method delegating calls to super. Cannot use anonymous classes, due
		 * to CODEMODEL-1. This hack clearly sucks.
		 */
		JMethod superBackgroundMethod = holder.activity.method(JMod.NONE, codeModel.VOID, "super_" + backgroundMethodName + "_");
		List<JVar> superParameters = new ArrayList<JVar>();
		JBlock superBackgroundBody = superBackgroundMethod.body();
		JInvocation superInvoke = JExpr._super().invoke(backgroundMethod);
		for (JVar param : parameters) {
			JVar superParam = superBackgroundMethod.param(param.type(), param.name());
			superParameters.add(superParam);
			superInvoke.arg(superParam);
		}
		superBackgroundBody.add(superInvoke);

		// Class extending Thread
		JDefinedClass threadClass = codeModel._class(JMod.NONE, holder.activity.fullName() + backgroundMethod.name() + "_Thread", ClassType.CLASS);
		threadClass._extends(Thread.class);

		JFieldVar threadActivityField = threadClass.field(JMod.PRIVATE | JMod.FINAL, holder.activity, "activity");

		// Constructor
		JMethod threadConstructor = threadClass.constructor(JMod.NONE);
		JVar threadActivityParam = threadConstructor.param(holder.activity, "activity");
		threadConstructor.body().assign(JExpr._this().ref(threadActivityField), threadActivityParam);

		List<JFieldVar> threadParamFields = new ArrayList<JFieldVar>();
		for (JVar param : parameters) {
			JVar threadParameter = threadConstructor.param(param.type(), param.name());
			JFieldVar threadParamField = threadClass.field(JMod.PRIVATE | JMod.FINAL, param.type(), param.name());
			threadParamFields.add(threadParamField);
			threadConstructor.body().assign(JExpr._this().ref(threadParamField), threadParameter);
		}

		JMethod runMethod = threadClass.method(JMod.PUBLIC, codeModel.VOID, "run");
		runMethod.annotate(Override.class);

		JBlock runMethodBody = runMethod.body();

		JTryBlock runTry = runMethodBody._try();

		JInvocation superBackgroundMethodInvoke = runTry.body().invoke(threadActivityField, superBackgroundMethod);

		for (JFieldVar paramField : threadParamFields) {
			superBackgroundMethodInvoke.arg(paramField);
		}

		JCatchBlock runCatch = runTry._catch(codeModel.ref(RuntimeException.class));
		JVar exceptionParam = runCatch.param("e");

		JClass logClass = codeModel.ref("android.util.Log");

		JInvocation errorInvoke = logClass.staticInvoke("e");

		errorInvoke.arg(JExpr.lit(holder.activity.name()));
		errorInvoke.arg(JExpr.lit("A runtime exception was thrown while executing code in a background thread"));
		errorInvoke.arg(exceptionParam);

		runCatch.body().add(errorInvoke);

		JBlock backgroundBody = backgroundMethod.body();
		JInvocation newThread = JExpr._new(threadClass).arg(JExpr._this());
		for (JVar param : parameters) {
			newThread.arg(param);
		}

		backgroundBody.add(newThread.invoke("start"));

	}

}
