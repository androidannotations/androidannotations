/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.core.handler;

import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;
import static org.androidannotations.helper.LogHelper.trimLogTag;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.Option;
import org.androidannotations.annotations.Trace;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

public class TraceHandler extends BaseAnnotationHandler<EComponentHolder> {

	public static final Option OPTION_TRACE = new Option("trace", "false");

	public TraceHandler(AndroidAnnotationsEnvironment environment) {
		super(Trace.class, environment);
	}

	@Override
	public boolean isEnabled() {
		return getEnvironment().getOptionBooleanValue(OPTION_TRACE);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.hasValidLogLevel(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		String tag = extractTag(executableElement);
		int level = executableElement.getAnnotation(Trace.class).level();

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = codeModelHelper.removeBody(method);

		JBlock methodBody = method.body();

		JInvocation isLoggableInvocation = getClasses().LOG.staticInvoke("isLoggable");
		isLoggableInvocation.arg(tag).arg(logLevelFromInt(level, getClasses().LOG));

		JConditional ifStatement = methodBody._if(isLoggableInvocation);

		JInvocation currentTimeInvoke = getClasses().SYSTEM.staticInvoke("currentTimeMillis");
		JBlock thenBody = ifStatement._then();

		// Log In
		String logMethodName = logMethodNameFromLevel(level);
		JInvocation logEnterInvoke = getClasses().LOG.staticInvoke(logMethodName);
		logEnterInvoke.arg(tag);

		logEnterInvoke.arg(getEnterMessage(method, executableElement));
		thenBody.add(logEnterInvoke);
		JVar startDeclaration = thenBody.decl(getCodeModel().LONG, "traceStart" + generationSuffix(), currentTimeInvoke);

		JTryBlock tryBlock;

		JVar result = null;
		if (method.type().fullName().equals("void")) {
			tryBlock = thenBody._try();
			tryBlock.body().add(previousMethodBody);
		} else {
			JInvocation superCall = codeModelHelper.getSuperCall(holder, method);
			result = thenBody.decl(getJClass(Object.class), "traceResult" + generationSuffix(), JExpr._null());
			tryBlock = thenBody._try();
			tryBlock.body().assign(result, superCall);
			tryBlock.body()._return(JExpr.cast(boxify(method.type()), result));
		}

		JBlock finallyBlock = tryBlock._finally();

		JVar durationDeclaration = finallyBlock.decl(getCodeModel().LONG, "traceDuration" + generationSuffix(), currentTimeInvoke.minus(startDeclaration));

		JInvocation logExitInvoke = getClasses().LOG.staticInvoke(logMethodName);
		logExitInvoke.arg(tag);

		logExitInvoke.arg(getExitMessage(executableElement, method, result, durationDeclaration));
		finallyBlock.add(logExitInvoke);

		JBlock elseBlock = ifStatement._else();

		elseBlock.add(previousMethodBody);
	}

	private AbstractJClass boxify(AbstractJType type) throws ClassNotFoundException {
		return getCodeModel().parseType(type.fullName()).boxify();
	}

	private IJExpression getExitMessage(ExecutableElement element, JMethod method, JVar result, JVar duration) throws ClassNotFoundException {
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

		JInvocation format = getJClass(String.class).staticInvoke("format");
		if (result == null) {
			format.arg("Exiting [" + methodName + "], duration in ms: %d");
		} else {
			format.arg("Exiting [" + methodName + " returning: %s], duration in ms: %d");
			if (method.type().isArray()) {
				AbstractJClass arraysClass = getJClass(Arrays.class);
				format.arg(arraysClass.staticInvoke("toString").arg(JExpr.cast(boxify(method.type()), result)));
			} else {
				format.arg(result);
			}
		}

		return format.arg(duration);
	}

	private IJExpression getEnterMessage(JMethod method, ExecutableElement element) {
		String methodName = getMethodName(element);

		List<JVar> params = method.params();
		if (params.isEmpty()) {
			// early exit if the method has no parameters
			return JExpr.lit("Entering [" + methodName + "()]");
		}

		AbstractJClass arraysClass = getJClass(Arrays.class);
		StringBuilder paramStr = new StringBuilder();
		List<IJExpression> paramExpressions = new ArrayList<>();
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

		JInvocation format = getJClass(String.class).staticInvoke("format");
		format.arg(JExpr.lit("Entering [" + methodName + "(" + paramStr + ")]"));
		for (IJExpression expr : paramExpressions) {
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

	private JFieldRef logLevelFromInt(int level, AbstractJClass logClass) {
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
		return trimLogTag(tag);
	}
}
