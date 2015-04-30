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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.FromHtml;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;

public class FromHtmlHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public FromHtmlHandler(ProcessingEnvironment processingEnvironment) {
		super(FromHtml.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.hasViewByIdAnnotation(element, validatedElements, valid);

		validatorHelper.extendsTextView(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.STRING, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(processHolder, element, IRClass.Res.STRING, true);

		JBlock methodBody = holder.getOnViewChangedBody();
		methodBody //
				._if(ref(fieldName).ne(_null())) //
				._then() //
				.invoke(ref(fieldName), "setText").arg(classes().HTML.staticInvoke("fromHtml").arg(holder.getContextRef().invoke("getString").arg(idRef)));
	}
}
