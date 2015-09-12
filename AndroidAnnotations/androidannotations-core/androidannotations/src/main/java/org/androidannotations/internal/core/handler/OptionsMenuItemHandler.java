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

import static com.helger.jcodemodel.JExpr._this;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JVar;

public class OptionsMenuItemHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	public OptionsMenuItemHandler(AndroidAnnotationsEnvironment environment) {
		super(OptionsMenuItem.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validation);

		validatorHelper.isDeclaredType(element, validation);

		validatorHelper.extendsMenuItem(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		String fieldName = element.getSimpleName().toString();
		JBlock body = holder.getOnCreateOptionsMenuMethodBody();
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam();

		JFieldRef idsRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.ID, true);
		body.assign(_this().ref(fieldName), menuParam.invoke("findItem").arg(idsRef));
	}
}
