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
import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.FragmentById;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;

public class FragmentByIdHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public FragmentByIdHandler(ProcessingEnvironment processingEnvironment) {
		super(FragmentById.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.extendsFragment(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();
		TypeElement nativeFragmentElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT);
		boolean isNativeFragment = nativeFragmentElement != null && annotationHelper.isSubtype(elementType, nativeFragmentElement.asType());

		JMethod findFragmentById;
		if (isNativeFragment) {
			findFragmentById = holder.getFindNativeFragmentById();
		} else {
			findFragmentById = holder.getFindSupportFragmentById();
		}

		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(processHolder, element, IRClass.Res.ID, true);

		JBlock methodBody = holder.getOnViewChangedBody();

		methodBody.assign(ref(fieldName), cast(refClass(typeQualifiedName), invoke(findFragmentById).arg(idRef)));

	}
}
