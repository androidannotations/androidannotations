package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasOnActivityResult;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class OnActivityResultHandler extends BaseAnnotationHandler<HasOnActivityResult> {

	public OnActivityResultHandler(ProcessingEnvironment processingEnvironment) {
		super(OnActivityResult.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		OnActivityResult onResultAnnotation = element.getAnnotation(OnActivityResult.class);
		validatorHelper.annotationValuePositiveAndInAShort(element, valid, onResultAnnotation.value());

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.hasOnResultMethodParameters(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, HasOnActivityResult holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int intentParameterPosition = -1;
		int resultCodeParameterPosition = -1;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.INTENT.equals(parameterType.toString())) {
				intentParameterPosition = i;
			} else if (parameterType.getKind().equals(TypeKind.INT) //
					|| CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				resultCodeParameterPosition = i;
			}
		}

		int requestCode = executableElement.getAnnotation(OnActivityResult.class).value();
		JBlock onActivityResultCase = holder.getOnActivityResultCaseBlock(requestCode);

		JExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation onResultInvocation = onActivityResultCase.invoke(activityRef, methodName);

		for (int i = 0; i < parameters.size(); i++) {
			if (i == intentParameterPosition) {
				JVar intentParameter = holder.getOnActivityResultDataParam();
				onResultInvocation.arg(intentParameter);
			} else if (i == resultCodeParameterPosition) {
				JVar resultCodeParameter = holder.getOnActivityResultResultCodeParam();
				onResultInvocation.arg(resultCodeParameter);
			}
		}
	}
}
