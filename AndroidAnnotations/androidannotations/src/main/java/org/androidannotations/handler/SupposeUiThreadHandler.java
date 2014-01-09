package org.androidannotations.handler;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JMethod;

import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class SupposeUiThreadHandler extends SupposeThreadHandler {

    private static final String METHOD_CHECK_UI_THREAD = "checkUiThread";

    private final APTCodeModelHelper helper = new APTCodeModelHelper();

    public SupposeUiThreadHandler(ProcessingEnvironment processingEnvironment) {
        super(SupposeUiThread.class, processingEnvironment);
    }

    @Override
    public void process(Element element, EComponentHolder holder) throws Exception {
        ExecutableElement executableElement = (ExecutableElement) element;

        JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);
        helper.removeBody(delegatingMethod);

        JClass bgExecutor = refClass(BackgroundExecutor.class);

        delegatingMethod.body().staticInvoke(bgExecutor, METHOD_CHECK_UI_THREAD);
        helper.callSuperMethod(delegatingMethod, holder, delegatingMethod.body());
    }
}
