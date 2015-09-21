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
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;

public class FragmentByIdHandler extends AbstractFragmentByHandler {

	public FragmentByIdHandler(AndroidAnnotationsEnvironment environment) {
		super(FragmentById.class, environment, "findFragmentById");
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);
	}

	@Override
	protected JMethod getFindFragmentMethod(boolean isNativeFragment, EComponentWithViewSupportHolder holder) {
		return isNativeFragment ? holder.getFindNativeFragmentById() : holder.getFindSupportFragmentById();
	}

	@Override
	protected JExpression getFragmentId(Element element, String fieldName) {
		return annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.ID, true);
	}
}
