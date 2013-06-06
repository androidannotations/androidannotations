package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class SeekBarProgressChangeHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public SeekBarProgressChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(SeekBarProgressChange.class, processingEnvironment);
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

		validatorHelper.hasSeekBarProgressChangeMethodParameters((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int seekBarViewParameterPosition = -1;
		int progressParameterPosition = -1;
		int fromUserParameterPosition = -1;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.SEEKBAR.equals(parameterType.toString())) {
				seekBarViewParameterPosition = i;
			} else if (parameterType.getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				progressParameterPosition = i;
			} else if (parameterType.getKind() == TypeKind.BOOLEAN || CanonicalNameConstants.BOOLEAN.equals(parameterType.toString())) {
				fromUserParameterPosition = i;
			}
		}

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = holder.getOnSeekBarChangeListenerHolder(idRef);
			JBlock methodBody = onSeekBarChangeListenerHolder.getOnProgressChangedBody();

			JExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == seekBarViewParameterPosition) {
					JVar seekBarViewParameter = onSeekBarChangeListenerHolder.getOnProgressChangedSeekBarParam();
					textChangeCall.arg(seekBarViewParameter);
				} else if (i == progressParameterPosition) {
					JVar progressParameter = onSeekBarChangeListenerHolder.getOnProgressChangedProgressParam();
					textChangeCall.arg(progressParameter);
				} else if (i == fromUserParameterPosition) {
					JVar fromUserParameter = onSeekBarChangeListenerHolder.getOnProgressChangedFromUserParam();
					textChangeCall.arg(fromUserParameter);
				}
			}

		}
	}
}
