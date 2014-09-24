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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldRef;

public class PreferenceByKeyHandler extends BaseAnnotationHandler<HasPreferences> {

	private IdAnnotationHelper annotationHelper;

	public PreferenceByKeyHandler(ProcessingEnvironment processingEnvironment) {
		super(PreferenceByKey.class, processingEnvironment);

	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);
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

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(processHolder, element, IRClass.Res.STRING, true);
		JClass preferenceClass = refClass(typeQualifiedName);
		JFieldRef fieldRef = ref(fieldName);

		holder.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

}
