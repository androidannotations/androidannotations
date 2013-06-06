/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.*;

public class FragmentByTagHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public FragmentByTagHandler(ProcessingEnvironment processingEnvironment) {
		super(FragmentByTag.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.extendsFragment(element, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
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
		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), invoke(findFragmentByTag).arg(lit(tagValue))));
	}
}
