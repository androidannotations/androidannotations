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

import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JFieldRef;

public class PreferenceScreenHandler extends BaseAnnotationHandler<HasPreferences> {

	private final AnnotationHelper annotationHelper;

	public PreferenceScreenHandler(ProcessingEnvironment processingEnvironment) {
		super(PreferenceScreen.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.extendsPreferenceActivityOrPreferenceFragment(element, valid);
		validatorHelper.hasEActivityOrEFragment(element, validatedElements, valid);
		validatorHelper.resIdsExist(element, IRClass.Res.XML, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);
	}

	@Override
	public void process(Element element, HasPreferences holder) throws Exception {
		JFieldRef preferenceId = annotationHelper.extractAnnotationFieldRefs(processHolder, element, getTarget(), rClass.get(IRClass.Res.XML), false).get(0);

		holder.getPreferenceScreenInitializationBlock().invoke("addPreferencesFromResource").arg(preferenceId);
	}
}
