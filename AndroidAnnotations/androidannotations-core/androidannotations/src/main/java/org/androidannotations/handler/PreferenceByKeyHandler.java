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

import static com.sun.codemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.process.ElementValidation;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldRef;

public class PreferenceByKeyHandler extends BaseAnnotationHandler<HasPreferences> {

	public PreferenceByKeyHandler(AndroidAnnotationsEnvironment environment) {
		super(PreferenceByKey.class, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, valid);

		validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element, valid);

		validatorHelper.isDeclaredType(element, valid);

		validatorHelper.extendsPreference(element, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.STRING, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);
	}

	@Override
	public void process(Element element, HasPreferences holder) throws Exception {
		String fieldName = element.getSimpleName().toString();

		TypeMirror prefFieldTypeMirror = element.asType();
		String typeQualifiedName = prefFieldTypeMirror.toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.STRING, true);
		JClass preferenceClass = refClass(typeQualifiedName);
		JFieldRef fieldRef = ref(fieldName);

		holder.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

}
