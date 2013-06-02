package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.holder.HasInstanceState;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import static com.sun.codemodel.JExpr.ref;

public class InstanceStateHandler extends BaseAnnotationHandler<HasInstanceState> {

    public InstanceStateHandler(ProcessingEnvironment processingEnvironment) {
        super(InstanceState.class, processingEnvironment);
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

        validatorHelper.isNotPrivate(element, valid);

        validatorHelper.canBeSavedAsInstanceState(element, valid);

        return valid.isValid();
    }

    @Override
    public void process(Element element, HasInstanceState holder) {
        String fieldName = element.getSimpleName().toString();

        JBlock saveStateBody = holder.getSaveStateMethodBody();
        JVar saveStateBundleParam = holder.getSaveStateBundleParam();
        JMethod restoreStateMethod = holder.getRestoreStateMethod();
        JBlock restoreStateBody = restoreStateMethod.body();
        JVar restoreStateBundleParam = holder.getRestoreStateBundleParam();

        AnnotationHelper annotationHelper = new AnnotationHelper(processingEnv);
        BundleHelper bundleHelper = new BundleHelper(annotationHelper, element);
        APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

        JFieldRef ref = ref(fieldName);
        saveStateBody.invoke(saveStateBundleParam, bundleHelper.getMethodNameToSave()).arg(fieldName).arg(ref);

        JInvocation restoreMethodCall = JExpr.invoke(restoreStateBundleParam, bundleHelper.getMethodNameToRestore()).arg(fieldName);
        if (bundleHelper.restoreCallNeedCastStatement()) {

            JClass jclass = codeModelHelper.typeMirrorToJClass(element.asType(), holder);
            JExpression castStatement = JExpr.cast(jclass, restoreMethodCall);
            restoreStateBody.assign(ref, castStatement);

            if (bundleHelper.restoreCallNeedsSuppressWarning()) {
                if (restoreStateMethod.annotations().size() == 0) {
                    restoreStateMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
                }
            }

        } else {
            restoreStateBody.assign(ref, restoreMethodCall);
        }
    }
}
