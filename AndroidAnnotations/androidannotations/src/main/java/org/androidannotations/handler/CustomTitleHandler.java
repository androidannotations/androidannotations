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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.CustomTitle;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;

public class CustomTitleHandler extends BaseAnnotationHandler<EActivityHolder> {

	private final AnnotationHelper annotationHelper;

	public CustomTitleHandler(ProcessingEnvironment processingEnvironment) {
		super(CustomTitle.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasEActivity(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {
		JBlock onViewChangedBody = holder.getOnViewChangedBody();

		JFieldRef contentViewId = annotationHelper.extractAnnotationFieldRefs(processHolder, element, getTarget(), rClass.get(IRClass.Res.LAYOUT), false).get(0);

		JFieldRef customTitleFeature = classes().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");
		holder.getInitBody().invoke("requestWindowFeature").arg(customTitleFeature);
		onViewChangedBody.add(holder.getContextRef().invoke("getWindow").invoke("setFeatureInt").arg(customTitleFeature).arg(contentViewId));
	}
}
