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
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.FoundPreferenceHolder;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public class PreferenceByKeyHandler extends BaseAnnotationHandler<HasPreferences> implements MethodInjectionHandler<HasPreferences> {

	private final InjectHelper<HasPreferences> injectHelper;

	public PreferenceByKeyHandler(AndroidAnnotationsEnvironment environment) {
		super(PreferenceByKey.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	protected void validate(Element element, ElementValidation valid) {
		injectHelper.validate(PreferenceByKey.class, element, valid);
		if (!valid.isValid()) {
			return;
		}

		if (element.getKind() == ElementKind.PARAMETER) {
			validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element.getEnclosingElement(), valid);
		} else {
			validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element, valid);
		}

		Element param = injectHelper.getParam(element);
		validatorHelper.isDeclaredType(param, valid);

		validatorHelper.extendsPreference(param, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.STRING, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);
	}

	@Override
	public void process(Element element, HasPreferences holder) throws Exception {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(HasPreferences holder) {
		return holder.getAddPreferencesFromResourceInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, HasPreferences holder, Element element, Element param) {
		TypeMirror prefFieldTypeMirror = param.asType();
		String typeQualifiedName = prefFieldTypeMirror.toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.STRING, true);
		AbstractJClass preferenceClass = getJClass(typeQualifiedName);

		IJAssignmentTarget preferenceHolderTarget = null;
		if (element.getKind() == ElementKind.FIELD) {
			preferenceHolderTarget = fieldRef;
		}
		FoundPreferenceHolder preferenceHolder = holder.getFoundPreferenceHolder(idRef, preferenceClass, preferenceHolderTarget);
		if (!preferenceHolder.getRef().equals(preferenceHolderTarget)) {
			targetBlock.add(fieldRef.assign(preferenceHolder.getOrCastRef(preferenceClass)));
		}
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, valid);
	}
}
