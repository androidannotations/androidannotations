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

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.DataBound;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EActivityHandler extends CoreBaseGeneratingAnnotationHandler<EActivityHolder> {

	public EActivityHandler(AndroidAnnotationsEnvironment environment) {
		super(EActivity.class, environment);
	}

	@Override
	public EActivityHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new EActivityHolder(environment, annotatedElement, getEnvironment().getAndroidManifest());
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		super.validate(element, valid);

		validatorHelper.extendsActivity(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.componentRegistered(element, getEnvironment().getAndroidManifest(), valid);

		coreValidatorHelper.checkDataBoundAnnotation(element, valid);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.LAYOUT, false);

		JFieldRef contentViewId = null;
		if (fieldRefs.size() == 1) {
			contentViewId = fieldRefs.get(0);
		}

		if (contentViewId == null) {
			return;
		}

		JBlock onCreateBody = holder.getOnCreate().body();
		JMethod setContentView = holder.getSetContentViewLayout();

		if (element.getAnnotation(DataBound.class) != null) {
			JFieldRef androidContentResId = getEnvironment().getRClass().get(IRClass.Res.ID).getIdStaticRef(android.R.id.content, getEnvironment());
			JVar contentView = onCreateBody.decl(getClasses().VIEW_GROUP, "contentView", JExpr.invoke("internalFindViewById").arg(androidContentResId));
			JFieldVar bindingField = holder.getDataBindingField();
			onCreateBody.assign(bindingField, holder.getDataBindingInflationExpression(contentViewId, contentView, false));
			onCreateBody.invoke(setContentView).arg(bindingField.invoke("getRoot")).arg(bindingField.invoke("getRoot").invoke("getLayoutParams"));
			holder.getOnDestroyBeforeSuperBlock().invoke(bindingField, "unbind");
		} else {
			onCreateBody.invoke(setContentView).arg(contentViewId);
		}
	}
}
