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
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JVar;

public class OptionsMenuItemHandler extends BaseAnnotationHandler<HasOptionsMenu> implements MethodInjectionHandler<HasOptionsMenu> {

	private final InjectHelper<HasOptionsMenu> injectHelper;

	public OptionsMenuItemHandler(AndroidAnnotationsEnvironment environment) {
		super(OptionsMenuItem.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(OptionsMenuItem.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		Element param = injectHelper.getParam(element);
		validatorHelper.isDeclaredType(param, validation);

		validatorHelper.extendsMenuItem(param, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(HasOptionsMenu holder) {
		return holder.getOnCreateOptionsMenuMethodBody();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, HasOptionsMenu holder, Element element, Element param) {
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam();
		JFieldRef idsRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.ID, true);
		targetBlock.add(fieldRef.assign(menuParam.invoke("findItem").arg(idsRef)));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, valid);
	}
}
