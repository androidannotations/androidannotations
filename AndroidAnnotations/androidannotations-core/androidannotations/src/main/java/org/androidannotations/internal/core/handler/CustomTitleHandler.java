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

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.CustomTitle;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public class CustomTitleHandler extends BaseAnnotationHandler<EActivityHolder> {

	public CustomTitleHandler(AndroidAnnotationsEnvironment environment) {
		super(CustomTitle.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.hasEActivity(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, validation);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {
		JBlock onViewChangedBody = holder.getOnViewChangedBodyBeforeInjectionBlock();

		JFieldRef contentViewId = annotationHelper.extractAnnotationFieldRefs(element, getTarget(), getEnvironment().getRClass().get(IRClass.Res.LAYOUT), false).get(0);

		JFieldRef customTitleFeature = getClasses().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");
		holder.getInitBodyInjectionBlock().invoke("requestWindowFeature").arg(customTitleFeature);
		onViewChangedBody.add(holder.getContextRef().invoke("getWindow").invoke("setFeatureInt").arg(customTitleFeature).arg(contentViewId));
	}
}
