package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.BeforeTextChange;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.TextWatcherHolder;
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

public class BeforeTextChangeHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper idAnnotationHelper;

	public BeforeTextChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(BeforeTextChange.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		idAnnotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.hasBeforeTextChangedMethodParameters((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int startParameterPosition = -1;
		int countParameterPosition = -1;
		int afterParameterPosition = -1;
		int charSequenceParameterPosition = -1;
		int viewParameterPosition = -1;
		TypeMirror viewParameterType = null;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			String parameterName = parameter.toString();
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.CHAR_SEQUENCE.equals(parameterType.toString())) {
				charSequenceParameterPosition = i;
			} else if (parameterType.getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				if ("start".equals(parameterName)) {
					startParameterPosition = i;
				} else if ("count".equals(parameterName)) {
					countParameterPosition = i;
				} else if ("after".equals(parameterName)) {
					afterParameterPosition = i;
				}
			} else {
				TypeMirror textViewType = idAnnotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.TEXT_VIEW).asType();
				if (idAnnotationHelper.isSubtype(parameterType, textViewType)) {
					viewParameterPosition = i;
					viewParameterType = parameterType;
				}
			}
		}

		List<JFieldRef> idsRefs = idAnnotationHelper.extractAnnotationFieldRefs(holder, element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			TextWatcherHolder textWatcherHolder = holder.getTextWatcherHolder(idRef, viewParameterType);
			JBlock methodBody = textWatcherHolder.getBeforeTextChangedBody();

			JExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == startParameterPosition) {
					JVar startParameter = textWatcherHolder.getBeforeTextChangedStartParam();
					textChangeCall.arg(startParameter);
				} else if (i == countParameterPosition) {
					JVar countParameter = textWatcherHolder.getBeforeTextChangedCountParam();
					textChangeCall.arg(countParameter);
				} else if (i == afterParameterPosition) {
					JVar afterParameter = textWatcherHolder.getBeforeTextChangedAfterParam();
					textChangeCall.arg(afterParameter);
				} else if (i == charSequenceParameterPosition) {
					JVar charSequenceParam = textWatcherHolder.getBeforeTextChangedCharSequenceParam();
					textChangeCall.arg(charSequenceParam);
				} else if (i == viewParameterPosition) {
					JVar viewParameter = textWatcherHolder.getTextViewVariable();
					textChangeCall.arg(viewParameter);
				}
			}
		}
	}
}
