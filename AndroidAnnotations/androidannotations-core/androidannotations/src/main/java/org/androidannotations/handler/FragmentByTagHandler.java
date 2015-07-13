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

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.process.ElementValidation;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;

public class FragmentByTagHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public FragmentByTagHandler(AndroidAnnotationsEnvironment environment) {
		super(FragmentByTag.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element,  validation);

		validatorHelper.extendsFragment(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();
		TypeMirror nativeFragmentType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT).asType();
		boolean isNativeFragment = annotationHelper.isSubtype(elementType, nativeFragmentType);

		JMethod findFragmentByTag;
		if (isNativeFragment) {
			findFragmentByTag = holder.getFindNativeFragmentByTag();
		} else {
			findFragmentByTag = holder.getFindSupportFragmentByTag();
		}

		String fieldName = element.getSimpleName().toString();
		FragmentByTag annotation = element.getAnnotation(FragmentByTag.class);
		String tagValue = annotation.value();
		if (tagValue.equals("")) {
			tagValue = fieldName;
		}

		JBlock methodBody = holder.getOnViewChangedBody();
		methodBody.assign(ref(fieldName), cast(refClass(typeQualifiedName), invoke(findFragmentByTag).arg(lit(tagValue))));
	}
}
