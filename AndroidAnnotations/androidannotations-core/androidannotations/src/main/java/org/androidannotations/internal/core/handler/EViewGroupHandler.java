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
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.DataBound;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EViewGroupHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;

public class EViewGroupHandler extends CoreBaseGeneratingAnnotationHandler<EViewGroupHolder> {

	public EViewGroupHandler(AndroidAnnotationsEnvironment environment) {
		super(EViewGroup.class, environment);
	}

	@Override
	public EViewGroupHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new EViewGroupHolder(environment, annotatedElement);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.extendsViewGroup(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, validation);

		coreValidatorHelper.checkDataBoundAnnotation(element, validation);
	}

	@Override
	public void process(Element element, EViewGroupHolder holder) {
		JFieldRef contentViewId = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.LAYOUT, false);
		if (contentViewId == null) {
			return;
		}

		if (element.getAnnotation(DataBound.class) != null) {
			holder.getSetContentViewBlock().assign(holder.getDataBindingField(), holder.getDataBindingInflationExpression(contentViewId, JExpr._this(), true));
		} else {
			holder.getSetContentViewBlock().invoke("inflate").arg(holder.getContextRef()).arg(contentViewId).arg(JExpr._this());
		}
	}
}
