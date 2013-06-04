package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class RestServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

    public RestServiceHandler(ProcessingEnvironment processingEnvironment) {
        super(RestService.class, processingEnvironment);
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

        validatorHelper.isNotPrivate(element, valid);

        validatorHelper.typeHasAnnotation(Rest.class, element, valid);

        return valid.isValid();
    }

    @Override
    public void process(Element element, EComponentHolder holder) {
        String fieldName = element.getSimpleName().toString();

        TypeMirror fieldTypeMirror = element.asType();
        String interfaceName = fieldTypeMirror.toString();

        String generatedClassName = interfaceName + ModelConstants.GENERATION_SUFFIX;

        JBlock methodBody = holder.getInit().body();

        JFieldRef field = JExpr.ref(fieldName);

        methodBody.assign(field, JExpr._new(holder.refClass(generatedClassName)));
    }
}
