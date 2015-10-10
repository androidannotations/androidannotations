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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.FromHtml;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public class FromHtmlHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public FromHtmlHandler(AndroidAnnotationsEnvironment environment) {
		super(FromHtml.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element,  validation);

		validatorHelper.hasViewByIdAnnotation(element, validation);

		validatorHelper.extendsTextView(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.STRING, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.STRING, true);

		JBlock methodBody = holder.getOnViewChangedBodyAfterInjectionBlock();
		methodBody //
				._if(ref(fieldName).ne(_null())) //
				._then() //
				.invoke(ref(fieldName), "setText").arg(getClasses().HTML.staticInvoke("fromHtml").arg(holder.getContextRef().invoke("getString").arg(idRef)));
	}
}
