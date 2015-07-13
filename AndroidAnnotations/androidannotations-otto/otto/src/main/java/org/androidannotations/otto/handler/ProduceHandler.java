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
package org.androidannotations.otto.handler;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.ValidatorParameterHelper;
import org.androidannotations.process.ElementValidation;

import javax.lang.model.element.ExecutableElement;

public class ProduceHandler extends AbstractOttoHandler {

	public ProduceHandler(AndroidAnnotationsEnvironment environment) {
		super(CanonicalNameConstants.PRODUCE, environment);
	}

	@Override
	protected ValidatorParameterHelper.Validator getParamValidator() {
		return validatorHelper.param.noParam();
	}

	@Override
	protected void validateReturnType(ExecutableElement executableElement, ElementValidation validation) {
		validatorHelper.returnTypeIsNotVoid(executableElement, validation);
	}
}
