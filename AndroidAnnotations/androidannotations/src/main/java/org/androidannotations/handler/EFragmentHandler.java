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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr.FALSE;
import static com.sun.codemodel.JExpr._null;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;

public class EFragmentHandler extends BaseGeneratingAnnotationHandler<EFragmentHolder> {

	public EFragmentHandler(ProcessingEnvironment processingEnvironment) {
		super(EFragment.class, processingEnvironment);
	}

	@Override
	public EFragmentHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EFragmentHolder(processHolder, annotatedElement);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.isAbstractOrHasEmptyConstructor(element, valid);

		validatorHelper.extendsFragment(element, valid);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {

		IdAnnotationHelper idAnnotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);

		JFieldRef contentViewId = idAnnotationHelper.extractOneAnnotationFieldRef(processHolder, element, IRClass.Res.LAYOUT, false);

		if (contentViewId != null) {

			JBlock block = holder.getSetContentViewBlock();
			JVar inflater = holder.getInflater();
			JVar container = holder.getContainer();

			JFieldVar contentView = holder.getContentView();

			boolean forceInjection = element.getAnnotation(EFragment.class).forceLayoutInjection();

			if (!forceInjection) {
				block._if(contentView.eq(_null())) //
						._then() //
						.assign(contentView, inflater.invoke("inflate").arg(contentViewId).arg(container).arg(FALSE));
			} else {
				block.assign(contentView, inflater.invoke("inflate").arg(contentViewId).arg(container).arg(FALSE));
			}

		}

	}
}
