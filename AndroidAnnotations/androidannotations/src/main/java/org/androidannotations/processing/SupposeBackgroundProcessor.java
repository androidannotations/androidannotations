package org.androidannotations.processing;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.helper.APTCodeModelHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr.lit;

public class SupposeBackgroundProcessor implements DecoratingElementProcessor {

    private static final String METHOD_CHECK_BG_THREAD = "checkBgThread";

    private final APTCodeModelHelper helper = new APTCodeModelHelper();

    @Override
    public String getTarget() {
        return SupposeBackground.class.getName();
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws JClassAlreadyExistsException {

        ExecutableElement executableElement = (ExecutableElement) element;

        holder.generateApiClass(element, BackgroundExecutor.class);

        JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);
        helper.removeBody(delegatingMethod);

        JClass bgExecutor = holder.refClass(BackgroundExecutor.class);

        SupposeBackground annotation = element.getAnnotation(SupposeBackground.class);
        String[] serial = annotation.serial();
        JInvocation invocation = bgExecutor.staticInvoke(METHOD_CHECK_BG_THREAD);
        for (String s : serial) {
            invocation.arg(lit(s));
        }

        delegatingMethod.body().add(invocation);
        helper.callSuperMethod(delegatingMethod, holder, delegatingMethod.body());
    }
}
