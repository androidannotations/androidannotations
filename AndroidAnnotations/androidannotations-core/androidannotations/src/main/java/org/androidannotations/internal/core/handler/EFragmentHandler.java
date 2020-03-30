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

import static com.helger.jcodemodel.JExpr.FALSE;
import static com.helger.jcodemodel.JExpr._null;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.DataBound;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;

public class EFragmentHandler extends CoreBaseGeneratingAnnotationHandler<EFragmentHolder> {

	public EFragmentHandler(AndroidAnnotationsEnvironment environment) {
		super(EFragment.class, environment);
	}

	@Override
	public EFragmentHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new EFragmentHolder(environment, annotatedElement);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.isAbstractOrHasEmptyConstructor(element, validation);

		validatorHelper.extendsFragment(element, validation);

		coreValidatorHelper.checkDataBoundAnnotation(element, validation);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {

		JFieldRef contentViewId = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.LAYOUT, false);

		if (contentViewId == null) {
			return;
		}

		JBlock block = holder.getSetContentViewBlock();
		JVar inflater = holder.getInflater();
		JVar container = holder.getContainer();

		JFieldVar contentView = holder.getContentView();

		boolean forceInjection = element.getAnnotation(EFragment.class).forceLayoutInjection();

		JBlock inflationBlock;

		if (!forceInjection) {
			inflationBlock = block._if(contentView.eq(_null())) //
					._then();
		} else {
			inflationBlock = block;
		}

		if (element.getAnnotation(DataBound.class) != null) {
			JFieldVar bindingField = holder.getDataBindingField();
			inflationBlock.assign(bindingField, holder.getDataBindingInflationExpression(contentViewId, container, false));
			inflationBlock.assign(contentView, bindingField.invoke("getRoot"));
			holder.getOnDestroyViewAfterSuperBlock().invoke(bindingField, "unbind");
			holder.clearInjectedView(bindingField);
		} else {
			inflationBlock.assign(contentView, inflater.invoke("inflate").arg(contentViewId).arg(container).arg(FALSE));
		}
	}
}
