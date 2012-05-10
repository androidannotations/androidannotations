/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.AfterTextChange;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.helper.TextWatcherHelper;
import com.googlecode.androidannotations.rclass.IRClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * @author Mathieu Boniface
 */
public class AfterTextChangeProcessor implements ElementProcessor {

	private final TextWatcherHelper helper;
	
	private final APTCodeModelHelper codeModelHelper;

	public AfterTextChangeProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
		helper = new TextWatcherHelper(processingEnv, getTarget(), rClass, codeModelHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return AfterTextChange.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int editableParameterPosition = -1;
		int viewParameterPosition = -1;
		TypeMirror viewParameterType = null;
		
		for (int i = 0 ; i < parameters.size() ; i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();
			
			if ("android.text.Editable".equals(parameterType.toString())) {
				editableParameterPosition = i;
			} else {
				TypeMirror textViewType = helper.typeElementFromQualifiedName("android.widget.TextView").asType();
				if (helper.isSubtype(parameterType, textViewType)) {
					viewParameterPosition = i;
					viewParameterType = parameterType;
				}
			}

		}

		AfterTextChange annotation = element.getAnnotation(AfterTextChange.class);
		
		List<JFieldRef> idsRefs = helper.extractFieldRefsFromAnnotationValues(element, annotation.value(), "AfterTextChanged", holder);

		for (JFieldRef idRef : idsRefs) {
			TextWatcherHolder textWatcherHolder = helper.getOrCreateListener(codeModel, holder, idRef, viewParameterType);

			JInvocation textChangeCall;
			JMethod methodToCall = textWatcherHolder.afterTextChangedMethod;

			JBlock previousBody = codeModelHelper.removeBody(methodToCall);
			JBlock methodBody = methodToCall.body();
			
			methodBody.add(previousBody);
			textChangeCall = methodBody.invoke(methodName);

			for (int i = 0 ; i < parameters.size() ; i++) {
				if (i == editableParameterPosition) {				
					JVar afterTextChangeEditableParam = codeModelHelper.findParameterByName(methodToCall, "s");
					textChangeCall.arg(afterTextChangeEditableParam);
				} else if (i == viewParameterPosition) {
					JVar viewParameter = textWatcherHolder.viewVariable;
					textChangeCall.arg(viewParameter);
				}
			}

		}

	}
	
}
