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
package org.androidannotations.processing;

import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.Trace;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
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

public class TraceProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return Trace.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();
		ExecutableElement executableElement = (ExecutableElement) element;

		String tag = extractTag(executableElement);
		int level = executableElement.getAnnotation(Trace.class).level();

		JMethod method = helper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = helper.removeBody(method);

		JBlock methodBody = method.body();

		JInvocation isLoggableInvocation = classes.LOG.staticInvoke("isLoggable");
		isLoggableInvocation.arg(JExpr.lit(tag)).arg(logLevelFromInt(level, classes.LOG));

		JConditional ifStatement = methodBody._if(isLoggableInvocation);

		JInvocation currentTimeInvoke = classes.SYSTEM.staticInvoke("currentTimeMillis");
		JBlock _thenBody = ifStatement._then();
		JVar startDeclaration = _thenBody.decl(codeModel.LONG, "start", currentTimeInvoke);

		String methodName = "[" + element.toString() + "]";

		// Log In
		String logMethodName = logMethodNameFromLevel(level);
		JInvocation logEnterInvoke = classes.LOG.staticInvoke(logMethodName);
		logEnterInvoke.arg(tag);

		JExpression enterMessage = JExpr.lit("Entering " + methodName);
		logEnterInvoke.arg(enterMessage);
		_thenBody.add(logEnterInvoke);

		JTryBlock tryBlock = _thenBody._try();

		tryBlock.body().add(previousMethodBody);

		JBlock finallyBlock = tryBlock._finally();

		JVar durationDeclaration = finallyBlock.decl(codeModel.LONG, "duration", currentTimeInvoke.minus(startDeclaration));

		JInvocation logExitInvoke = classes.LOG.staticInvoke(logMethodName);
		logExitInvoke.arg(tag);

		JExpression exitMessage = JExpr.lit("Exiting " + methodName + ", duration in ms: ").plus(durationDeclaration);
		logExitInvoke.arg(exitMessage);
		finallyBlock.add(logExitInvoke);

		JBlock elseBlock = ifStatement._else();

		elseBlock.add(previousMethodBody);
	}

	private String logMethodNameFromLevel(int level) {
		switch (level) {
		case LOG_DEBUG:
			return "d";
		case LOG_VERBOSE:
			return "v";
		case LOG_INFO:
			return "i";
		case LOG_WARN:
			return "w";
		case LOG_ERROR:
			return "e";
		default:
			throw new IllegalArgumentException("Unrecognized Log level : " + level);
		}
	}

	private JFieldRef logLevelFromInt(int level, JClass logClass) {
		switch (level) {
		case LOG_DEBUG:
			return logClass.staticRef("DEBUG");
		case LOG_VERBOSE:
			return logClass.staticRef("VERBOSE");
		case LOG_INFO:
			return logClass.staticRef("INFO");
		case LOG_WARN:
			return logClass.staticRef("WARN");
		case LOG_ERROR:
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
