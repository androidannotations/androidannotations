package org.androidannotations.handler;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import org.androidannotations.annotations.Background;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;

public class BackgroundHandler extends AbstractRunnableHandler {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public BackgroundHandler(ProcessingEnvironment processingEnvironment) {
		super(Background.class, processingEnvironment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		holder.generateApiClass(element, BackgroundExecutor.class);

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JDefinedClass anonymousRunnableClass = codeModelHelper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		Background annotation = element.getAnnotation(Background.class);
		long delay = annotation.delay();

		JClass backgroundExecutorClass = holder.refClass(BackgroundExecutor.class);
		JInvocation executeCall;

		if (delay == 0) {
			executeCall = backgroundExecutorClass.staticInvoke("execute").arg(_new(anonymousRunnableClass));
		} else {
			executeCall = backgroundExecutorClass.staticInvoke("executeDelayed").arg(_new(anonymousRunnableClass)).arg(lit(delay));
		}

		delegatingMethod.body().add(executeCall);
	}
}
