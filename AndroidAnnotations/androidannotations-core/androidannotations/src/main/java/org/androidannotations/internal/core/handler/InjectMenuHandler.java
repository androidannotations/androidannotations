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
import org.androidannotations.annotations.InjectMenu;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.HasOptionsMenu;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;

public class InjectMenuHandler extends BaseAnnotationHandler<HasOptionsMenu> implements MethodInjectionHandler<HasOptionsMenu> {

	private final InjectHelper<HasOptionsMenu> injectHelper;

	public InjectMenuHandler(AndroidAnnotationsEnvironment environment) {
		super(InjectMenu.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		injectHelper.validate(InjectMenu.class, element, valid);
		if (!valid.isValid()) {
			return;
		}

		Element param = injectHelper.getParam(element);
		validatorHelper.isDeclaredType(param, valid);

		validatorHelper.extendsMenu(param, valid);

		validatorHelper.isNotPrivate(element, valid);
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
		targetBlock.add(fieldRef.assign(holder.getOnCreateOptionsMenuMenuParam()));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, valid);
	}
}
