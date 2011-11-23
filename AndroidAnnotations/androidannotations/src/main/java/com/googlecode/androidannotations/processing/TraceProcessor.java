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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import android.util.Log;

import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class TraceProcessor implements ElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return Trace.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getEnclosingActivityHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		String tag = extractTag(executableElement);
		int level = executableElement.getAnnotation(Trace.class).level();

		JClass logClass = holder.refClass("android.util.Log");
		JClass systemClass = holder.refClass(System.class);

		JMethod method = helper.overrideAnnotatedMethod(executableElement, holder);

		JBlock methodBody = method.body();

		JInvocation isLoggableInvocation = logClass.staticInvoke("isLoggable");
		isLoggableInvocation.arg(JExpr.lit(tag)).arg(logLevelFromInt(level, logClass));

		JConditional ifStatement = methodBody._if(isLoggableInvocation);

		JInvocation currentTimeInvoke = systemClass.staticInvoke("currentTimeMillis");
		JVar startDeclaration = ifStatement._then().decl(codeModel.LONG, "start", currentTimeInvoke);

		JTryBlock tryBlock = ifStatement._then()._try();
		helper.callSuperMethod(method, codeModel, holder, tryBlock.body());

		JBlock finallyBlock = tryBlock._finally();

		JVar durationDeclaration = finallyBlock.decl(codeModel.LONG, "duration", currentTimeInvoke.minus(startDeclaration));

		String logMethodString = logMethodNameFromLevel(level);
		JInvocation logInvoke = logClass.staticInvoke(logMethodString);
		logInvoke.arg(tag);

		String methodName = element.getSimpleName().toString();
		JExpression message = JExpr.lit("out " + methodName + ", duration in ms: ").plus(durationDeclaration);
		logInvoke.arg(message);
		finallyBlock.add(logInvoke);

		JBlock elseBlock = ifStatement._else();
		helper.callSuperMethod(method, codeModel, holder, elseBlock);
	}

	private String logMethodNameFromLevel(int level) {
		switch (level) {
		case Log.DEBUG:
			return "d";
		case Log.VERBOSE:
			return "v";
		case Log.INFO:
			return "i";
		case Log.WARN:
			return "w";
		case Log.ERROR:
			return "e";
		default:
			throw new IllegalArgumentException("Unrecognized Log level : " + level);
		}
	}

	private JFieldRef logLevelFromInt(int level, JClass logClass) {
		switch (level) {
		case Log.DEBUG:
			return logClass.staticRef("DEBUG");
		case Log.VERBOSE:
			return logClass.staticRef("VERBOSE");
		case Log.INFO:
			return logClass.staticRef("INFO");
		case Log.WARN:
			return logClass.staticRef("WARN");
		case Log.ERROR:
			return logClass.staticRef("ERROR");
		default:
			throw new IllegalArgumentException("Unrecognized log level. Given value:" + level);
		}
	}

	private String extractTag(Element element) {
		Trace annotation = element.getAnnotation(Trace.class);
		String tag = annotation.tag();
		if (Trace.DEFAULT_TAG.equals(tag)) {
			tag = element.getEnclosingElement().getSimpleName().toString();
		}
		if (tag.length() > 23) {
			tag = tag.substring(0, 23);
		}
		return tag;
	}

}
