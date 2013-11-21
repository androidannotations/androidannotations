package org.androidannotations.processing;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;

import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.helper.APTCodeModelHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class SupposeUiThreadProcessor implements DecoratingElementProcessor {

    private static final String METHOD_CHECK_UI_THREAD = "checkUiThread";

    private final APTCodeModelHelper helper = new APTCodeModelHelper();

    @Override
    public String getTarget() {
        return SupposeUiThread.class.getName();
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws JClassAlreadyExistsException {

        ExecutableElement executableElement = (ExecutableElement) element;

        holder.generateApiClass(element, BackgroundExecutor.class);

        JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);
        helper.removeBody(delegatingMethod);

        JClass bgExecutor = holder.refClass(BackgroundExecutor.class);

        delegatingMethod.body().staticInvoke(bgExecutor, METHOD_CHECK_UI_THREAD);
        helper.callSuperMethod(delegatingMethod, holder, delegatingMethod.body());
    }
}
