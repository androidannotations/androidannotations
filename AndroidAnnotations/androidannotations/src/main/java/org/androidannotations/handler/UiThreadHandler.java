package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;

public class UiThreadHandler extends AbstractRunnableHandler {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public UiThreadHandler(ProcessingEnvironment processingEnvironment) {
		super(UiThread.class, processingEnvironment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;
		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JDefinedClass anonymousRunnableClass = codeModelHelper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		UiThread annotation = element.getAnnotation(UiThread.class);
		long delay = annotation.delay();

		if (delay == 0) {
			delegatingMethod.body().invoke(holder.getHandler(), "post").arg(_new(anonymousRunnableClass));
		} else {
			delegatingMethod.body().invoke(holder.getHandler(), "postDelayed").arg(_new(anonymousRunnableClass)).arg(lit(delay));
		}
	}
}
