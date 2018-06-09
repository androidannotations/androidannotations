/**
 * Copyright (C) 2016-2018 the AndroidAnnotations project
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.BindingObject;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.internal.rclass.ProjectRClassFinder;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;

public class BindingObjectHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> implements MethodInjectionHandler<EComponentWithViewSupportHolder> {

	private final InjectHelper<EComponentWithViewSupportHolder> injectHelper;

	public BindingObjectHandler(AndroidAnnotationsEnvironment environment) {
		super(BindingObject.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		if (element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.PARAMETER) {
			ExecutableElement methodElement = (ExecutableElement) (element.getKind() == ElementKind.METHOD ? element : element.getEnclosingElement());

			validatorHelper.param.extendsType(CanonicalNameConstants.VIEW_DATA_BINDING).validate(methodElement, validation);

			if (!validation.isValid()) {
				return;
			}
		}

		injectHelper.validate(BindingObject.class, element, validation);

		if (validation.isValid()) {
			validatorHelper.isNotPrivate(element, validation);
			validatorHelper.extendsType(injectHelper.getParam(element), CanonicalNameConstants.VIEW_DATA_BINDING, validation);
		}
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		coreValidatorHelper.checkDataBoundAnnotation(element.getEnclosingElement(), valid);
		coreValidatorHelper.hasEActivityOrEFragmentOrEViewGroup(element.getEnclosingElement(), element, valid);
		coreValidatorHelper.enclosingElementHasDataBoundAnnotation(element, valid);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		injectHelper.process(element, holder);

		if (element.getKind() == ElementKind.FIELD && holder instanceof EFragment) {
			((EFragmentHolder) holder).clearInjectedView(JExpr._this().ref(element.getSimpleName().toString()));
		}
	}

	@Override
	public JBlock getInvocationBlock(EComponentWithViewSupportHolder holder) {
		return holder.getOnViewChangedBodyAfterInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentWithViewSupportHolder holder, Element element, Element param) {
		String bindingClassQualifiedName;

		if (!param.asType().toString().contains(".")) { // the class is generated in this round, so only the simple name is available
			String resourcePackageName = getEnvironment().getOptionValue(ProjectRClassFinder.OPTION_RESOURCE_PACKAGE_NAME);

			if (resourcePackageName == null) {
				resourcePackageName = getEnvironment().getAndroidManifest().getApplicationPackage();
			}

			bindingClassQualifiedName = resourcePackageName + ".databinding." + param.asType().toString();
		} else {
			bindingClassQualifiedName = param.asType().toString();
		}

		JDirectClass bindingClass = getCodeModel().directClass(bindingClassQualifiedName);
		targetBlock.add(fieldRef.assign(JExpr.cast(bindingClass, holder.getDataBindingField())));
	}
}
