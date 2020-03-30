/**
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
import org.androidannotations.annotations.DataBound;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

public class DataBoundHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public DataBoundHandler(AndroidAnnotationsEnvironment environment) {
		super(DataBound.class, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		coreValidatorHelper.hasDataBindingOnClasspath(validation);
		coreValidatorHelper.hasEActivityOrEFragmentOrEViewGroup(element, element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		// nothing to do
	}
}
