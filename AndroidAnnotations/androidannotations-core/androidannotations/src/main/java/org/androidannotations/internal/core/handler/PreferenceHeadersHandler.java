/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
import org.androidannotations.annotations.PreferenceHeaders;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasPreferenceHeaders;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JVar;

public class PreferenceHeadersHandler extends BaseAnnotationHandler<HasPreferenceHeaders> {

	public PreferenceHeadersHandler(AndroidAnnotationsEnvironment environment) {
		super(PreferenceHeaders.class, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation valid) {
		validatorHelper.isPreferenceFragmentClassPresent(element, valid);
		validatorHelper.extendsPreferenceActivity(element, valid);
		validatorHelper.hasEActivity(element, valid);
		validatorHelper.resIdsExist(element, IRClass.Res.XML, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);
	}

	@Override
	public void process(Element element, HasPreferenceHeaders holder) throws Exception {
		JFieldRef headerId = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.XML, false).get(0);

		JBlock block = holder.getOnBuildHeadersBlock();
		JVar targetParam = holder.getOnBuildHeadersTargetParam();
		block.invoke("loadHeadersFromResource").arg(headerId).arg(targetParam);
		block.invoke(JExpr._super(), "onBuildHeaders").arg(targetParam);
	}
}
