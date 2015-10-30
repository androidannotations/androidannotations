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

import java.util.List;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.holder.OnCreateOptionMenuDelegate;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JVar;

public class OptionsMenuHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	public OptionsMenuHandler(AndroidAnnotationsEnvironment environment) {
		super(OptionsMenu.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.hasEActivityOrEFragment(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.MENU, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, validation);
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		Boolean overrideParent = false;
		Boolean cleanBeforeInflate = false;
		if (holder.getAnnotatedElement().getAnnotation(OptionsMenu.class) != null) {
			overrideParent = annotationHelper.extractAnnotationParameter(holder.getAnnotatedElement(), "overrideParent");
			cleanBeforeInflate = annotationHelper.extractAnnotationParameter(holder.getAnnotatedElement(), "cleanBeforeInflate");
		}
		OnCreateOptionMenuDelegate.CreateOptionAnnotationData createOptionAnnotationData = new OnCreateOptionMenuDelegate.CreateOptionAnnotationData(overrideParent, cleanBeforeInflate);
		JBlock body = holder.getOnCreateOptionsMenuMethodBody(createOptionAnnotationData);
		JVar menuInflater = holder.getOnCreateOptionsMenuMenuInflaterVar(createOptionAnnotationData);
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam(createOptionAnnotationData);
		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.MENU, false);
		for (JFieldRef optionsMenuRefId : fieldRefs) {
			body.invoke(menuInflater, "inflate").arg(optionsMenuRefId).arg(menuParam);
		}
	}
}
