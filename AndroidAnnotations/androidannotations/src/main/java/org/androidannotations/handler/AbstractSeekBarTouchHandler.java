package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.OnSeekBarChangeListenerHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public abstract class AbstractSeekBarTouchHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public AbstractSeekBarTouchHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.hasSeekBarTouchTrackingMethodParameters((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = holder.getOnSeekBarChangeListenerHolder(idRef);
			JBlock methodBody = getMethodBodyToCall(onSeekBarChangeListenerHolder);

			JExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			ExecutableElement executableElement = (ExecutableElement) element;
			List<? extends VariableElement> parameters = executableElement.getParameters();

			if (parameters.size() == 1) {
				JVar progressParameter = getMethodParamToPass(onSeekBarChangeListenerHolder);
				textChangeCall.arg(progressParameter);
			}
		}
	}

	protected abstract JBlock getMethodBodyToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder);
	protected abstract JVar getMethodParamToPass(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder);
}
