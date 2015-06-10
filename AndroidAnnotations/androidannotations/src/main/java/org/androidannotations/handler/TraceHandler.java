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
package org.androidannotations.handler;

import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.Trace;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class TraceHandler extends BaseAnnotationHandler<EComponentHolder> {

	public TraceHandler(ProcessingEnvironment processingEnvironment) {
		super(Trace.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.hasValidLogLevel(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		String tag = extractTag(executableElement);
		int level = executableElement.getAnnotation(Trace.class).level();

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = codeModelHelper.removeBody(method);

		JBlock methodBody = method.body();

		JInvocation isLoggableInvocation = classes().LOG.staticInvoke("isLoggable");
		isLoggableInvocation.arg(JExpr.lit(tag)).arg(logLevelFromInt(level, classes().LOG));

		JConditional ifStatement = methodBody._if(isLoggableInvocation);

		JInvocation currentTimeInvoke = classes().SYSTEM.staticInvoke("currentTimeMillis");
		JBlock thenBody = ifStatement._then();

		// Log In
		String logMethodName = logMethodNameFromLevel(level);
		JInvocation logEnterInvoke = classes().LOG.staticInvoke(logMethodName);
		logEnterInvoke.arg(tag);

		logEnterInvoke.arg(getEnterMessage(method, executableElement));
		thenBody.add(logEnterInvoke);
		JVar startDeclaration = thenBody.decl(codeModel().LONG, "start", currentTimeInvoke);

		JTryBlock tryBlock;

		JVar result = null;
		if (method.type().fullName().equals("void")) {
			tryBlock = thenBody._try();
			tryBlock.body().add(previousMethodBody);
		} else {
			JInvocation superCall = codeModelHelper.getSuperCall(holder, method);
			result = thenBody.decl(refClass(Object.class), "result", JExpr._null());
			tryBlock = thenBody._try();
			tryBlock.body().assign(result, superCall);
			tryBlock.body()._return(JExpr.cast(boxify(method.type()), result));
		}

		JBlock finallyBlock = tryBlock._finally();

		JVar durationDeclaration = finallyBlock.decl(codeModel().LONG, "duration", currentTimeInvoke.minus(startDeclaration));

		JInvocation logExitInvoke = classes().LOG.staticInvoke(logMethodName);
		logExitInvoke.arg(tag);

		logExitInvoke.arg(getExitMessage(executableElement, method, result, durationDeclaration));
		finallyBlock.add(logExitInvoke);

		JBlock elseBlock = ifStatement._else();

		elseBlock.add(previousMethodBody);
	}

	private JClass boxify(JType type) throws ClassNotFoundException {
		return codeModel().parseType(type.fullName()).boxify();
	}

	private JExpression getExitMessage(ExecutableElement element, JMethod method, JVar result, JVar duration) throws ClassNotFoundException {
		String methodName = getMethodName(element);

		List<JVar> params = method.params();
		StringBuilder paramStr = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) {
				paramStr.append(", ");
			}
			JVar var = params.get(i);
			paramStr.append(var.type().name());
		}

		methodName += "(" + paramStr.toString() + ")";

		JInvocation format = refClass(String.class).staticInvoke("format");
		if (result == null) {
			format.arg("Exiting [" + methodName + "], duration in ms: %d");
		} else {
			format.arg("Exiting [" + methodName + " returning: %s], duration in ms: %d");
			if (method.type().isArray()) {
				JClass arraysClass = refClass(Arrays.class);
				format.arg(arraysClass.staticInvoke("toString").arg(JExpr.cast(boxify(method.type()), result)));
			} else {
				format.arg(result);
			}
		}

		return format.arg(duration);
	}

	private JExpression getEnterMessage(JMethod method, ExecutableElement element) {
		String methodName = getMethodName(element);

		List<JVar> params = method.params();
		if (params.isEmpty()) {
			// early exit if the method has no parameters
			return JExpr.lit("Entering [" + methodName + "()]");
		}

		JClass arraysClass = refClass(Arrays.class);
		StringBuilder paramStr = new StringBuilder();
		List<JExpression> paramExpressions = new ArrayList<>();
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) {
				paramStr.append(", ");
			}
			JVar var = params.get(i);
			paramStr.append(var.name()).append(" = %s");
			if (var.type().isArray()) {
				paramExpressions.add(arraysClass.staticInvoke("toString").arg(var));
			} else {
				paramExpressions.add(var);
			}
		}

		JInvocation format = refClass(String.class).staticInvoke("format");
		format.arg(JExpr.lit("Entering [" + methodName + "(" + paramStr + ")]"));
		for (JExpression expr : paramExpressions) {
			format.arg(expr);
		}

		return format;
	}

	private String getMethodName(ExecutableElement element) {
		String returnType = element.getReturnType().toString();
		String simpleName = element.getSimpleName().toString();
		return returnType + " " + simpleName;
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
