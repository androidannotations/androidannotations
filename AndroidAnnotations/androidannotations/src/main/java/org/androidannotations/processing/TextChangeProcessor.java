/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.TextChange;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.TextWatcherHelper;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 */
public class TextChangeProcessor implements DecoratingElementProcessor {

	private final TextWatcherHelper helper;

	private final APTCodeModelHelper codeModelHelper;

	public TextChangeProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
		helper = new TextWatcherHelper(processingEnv, getTarget(), rClass, codeModelHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return TextChange.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

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
				TypeMirror textViewType = helper.typeElementFromQualifiedName(CanonicalNameConstants.TEXT_VIEW).asType();
				if (helper.isSubtype(parameterType, textViewType)) {
					viewParameterPosition = i;
					viewParameterType = parameterType;
				}
			}
		}

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			TextWatcherHolder textWatcherHolder = helper.getOrCreateListener(codeModel, holder, idRef, viewParameterType);

			JInvocation textChangeCall;
			JMethod methodToCall = textWatcherHolder.onTextChangedMethod;

			JBlock previousBody = codeModelHelper.removeBody(methodToCall);
			JBlock methodBody = methodToCall.body();

			methodBody.add(previousBody);
			JExpression activityRef = holder.generatedClass.staticRef("this");
			textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == startParameterPosition) {
					JVar startParameter = codeModelHelper.findParameterByName(methodToCall, "start");
					textChangeCall.arg(startParameter);
				} else if (i == countParameterPosition) {
					JVar countParameter = codeModelHelper.findParameterByName(methodToCall, "count");
					textChangeCall.arg(countParameter);
				} else if (i == beforeParameterPosition) {
					JVar beforeParameter = codeModelHelper.findParameterByName(methodToCall, "before");
					textChangeCall.arg(beforeParameter);
				} else if (i == charSequenceParameterPosition) {
					JVar charSequenceParam = codeModelHelper.findParameterByName(methodToCall, "s");
					textChangeCall.arg(charSequenceParam);
				} else if (i == viewParameterPosition) {
					JVar viewParameter = textWatcherHolder.viewVariable;
					textChangeCall.arg(viewParameter);
				}
			}

		}

	}

}
