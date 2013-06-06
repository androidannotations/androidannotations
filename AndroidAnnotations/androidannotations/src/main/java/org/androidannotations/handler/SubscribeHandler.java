package org.androidannotations.handler;

import com.sun.codemodel.JMethod;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static org.androidannotations.helper.CanonicalNameConstants.SUBSCRIBE;

public class SubscribeHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final TargetAnnotationHelper annotationHelper;
	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public SubscribeHandler(ProcessingEnvironment processingEnvironment) {
		super(CanonicalNameConstants.SUBSCRIBE, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		if (!annotationHelper.enclosingElementHasEnhancedComponentAnnotation(element)) {
			return false;
		}

		IsValid valid = new IsValid();

		ExecutableElement executableElement = (ExecutableElement) element;

		/*
		 * We check that twice to skip invalid annotated elements
		 */
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(executableElement, validatedElements, valid);

		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.isPublic(element, valid);

		validatorHelper.doesntThrowException(executableElement, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.param.hasExactlyOneParameter(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		delegatingMethod.annotate(holder.refClass(SUBSCRIBE));
	}
}
