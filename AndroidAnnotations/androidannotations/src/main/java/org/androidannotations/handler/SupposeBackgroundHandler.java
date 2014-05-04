package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr.lit;

public class SupposeBackgroundHandler extends SupposeThreadHandler {

    private static final String METHOD_CHECK_BG_THREAD = "checkBgThread";

    private final APTCodeModelHelper helper = new APTCodeModelHelper();

    public SupposeBackgroundHandler(ProcessingEnvironment processingEnvironment) {
        super(SupposeBackground.class, processingEnvironment);
    }

    @Override
    public void process(Element element, EComponentHolder holder) throws Exception {
        ExecutableElement executableElement = (ExecutableElement) element;

        JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);

        JClass bgExecutor = refClass(BackgroundExecutor.class);

        SupposeBackground annotation = element.getAnnotation(SupposeBackground.class);
        String[] serial = annotation.serial();
        JInvocation invocation = bgExecutor.staticInvoke(METHOD_CHECK_BG_THREAD);
        for (String s : serial) {
            invocation.arg(lit(s));
        }

        JBlock body = delegatingMethod.body();
        body.pos(0);
        body.add(invocation);
        body.pos(body.getContents().size());
    }
}
