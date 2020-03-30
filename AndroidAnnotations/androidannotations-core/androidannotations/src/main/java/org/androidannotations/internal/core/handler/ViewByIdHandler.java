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

import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.holder.FoundViewHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public class ViewByIdHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> implements MethodInjectionHandler<EComponentWithViewSupportHolder> {

	private final InjectHelper<EComponentWithViewSupportHolder> injectHelper;

	public ViewByIdHandler(AndroidAnnotationsEnvironment environment) {
		super(ViewById.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(ViewById.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		Element param = injectHelper.getParam(element);
		validatorHelper.isDeclaredType(param, validation);

		validatorHelper.extendsView(param, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		injectHelper.process(element, holder);
		if (holder instanceof EFragmentHolder && isFieldInjection(element)) {
			String fieldName = element.getSimpleName().toString();
			((EFragmentHolder) holder).clearInjectedView(ref(fieldName));
		}
	}

	private boolean isFieldInjection(Element element) {
		Element enclosingElement = element.getEnclosingElement();
		return !((element instanceof ExecutableElement) || (enclosingElement instanceof ExecutableElement));
	}

	@Override
	public JBlock getInvocationBlock(EComponentWithViewSupportHolder holder) {
		return holder.getOnViewChangedBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentWithViewSupportHolder holder, Element element, Element param) {
		TypeMirror uiFieldTypeMirror = param.asType();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.ID, true);
		AbstractJClass viewClass = codeModelHelper.typeMirrorToJClass(uiFieldTypeMirror);

		IJAssignmentTarget viewHolderTarget = null;
		if (element.getKind() == ElementKind.FIELD) {
			viewHolderTarget = fieldRef;
		}
		FoundViewHolder viewHolder = holder.getFoundViewHolder(idRef, viewClass, viewHolderTarget);
		if (!viewHolder.getRef().equals(viewHolderTarget)) {
			targetBlock.add(fieldRef.assign(viewHolder.getOrCastRef(viewClass)));
		}
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, valid);
	}
}
