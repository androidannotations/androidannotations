package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.Trace;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static org.androidannotations.helper.AndroidConstants.*;

public class TraceHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public TraceHandler(ProcessingEnvironment processingEnvironment) {
		super(Trace.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.hasValidLogLevel(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ProcessHolder.Classes classes = holder.classes();
		JCodeModel codeModel = holder.codeModel();
		ExecutableElement executableElement = (ExecutableElement) element;

		String tag = extractTag(executableElement);
		int level = executableElement.getAnnotation(Trace.class).level();

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = codeModelHelper.removeBody(method);

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
