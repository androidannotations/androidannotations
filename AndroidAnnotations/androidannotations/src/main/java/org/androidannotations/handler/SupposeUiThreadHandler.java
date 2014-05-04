package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
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
        JBlock body = delegatingMethod.body();

        JClass bgExecutor = refClass(BackgroundExecutor.class);

        body.pos(0);
        body.staticInvoke(bgExecutor, METHOD_CHECK_UI_THREAD);
        body.pos(body.getContents().size());
    }
}
