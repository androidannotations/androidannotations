/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.handler;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.TextChange;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.TextWatcherHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class TextChangeHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper idAnnotationHelper;

	public TextChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(TextChange.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		idAnnotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.hasTextChangedMethodParameters((ExecutableElement) element, valid);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int startParameterPosition = -1;
		int countParameterPosition = -1;
		int beforeParameterPosition = -1;
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
				} else if ("before".equals(parameterName)) {
					beforeParameterPosition = i;
				}
			} else {
				TypeMirror textViewType = idAnnotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.TEXT_VIEW).asType();
				if (idAnnotationHelper.isSubtype(parameterType, textViewType)) {
					viewParameterPosition = i;
					viewParameterType = parameterType;
				}
			}
		}

		List<JFieldRef> idsRefs = idAnnotationHelper.extractAnnotationFieldRefs(processHolder, element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			TextWatcherHolder textWatcherHolder = holder.getTextWatcherHolder(idRef, viewParameterType);
			JBlock methodBody = textWatcherHolder.getOnTextChangedBody();

			JExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == startParameterPosition) {
					JVar startParameter = textWatcherHolder.getOnTextChangedStartParam();
					textChangeCall.arg(startParameter);
				} else if (i == countParameterPosition) {
					JVar countParameter = textWatcherHolder.getOnTextChangedCountParam();
					textChangeCall.arg(countParameter);
				} else if (i == beforeParameterPosition) {
					JVar beforeParameter = textWatcherHolder.getOnTextChangedBeforeParam();
					textChangeCall.arg(beforeParameter);
				} else if (i == charSequenceParameterPosition) {
					JVar charSequenceParam = textWatcherHolder.getOnTextChangedCharSequenceParam();
					textChangeCall.arg(charSequenceParam);
				} else if (i == viewParameterPosition) {
					JVar viewParameter = textWatcherHolder.getTextViewVariable();
					textChangeCall.arg(viewParameter);
				}
			}

		}
	}
}
