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
package org.androidannotations.internal.core.handler;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;

public abstract class AbstractFragmentByHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public AbstractFragmentByHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.extendsFragment(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public final void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();
		TypeElement nativeFragmentElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT);
		boolean isNativeFragment = nativeFragmentElement != null && annotationHelper.isSubtype(elementType, nativeFragmentElement.asType());

		JMethod findFragmentMethod = getFindFragmentMethod(isNativeFragment, holder);

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.getOnViewChangedBody();

		methodBody.assign(ref(fieldName), cast(getJClass(typeQualifiedName), invoke(findFragmentMethod).arg(getFragmentId(element, fieldName))));
	}

	protected abstract JMethod getFindFragmentMethod(boolean isNativeFragment, EComponentWithViewSupportHolder holder);

	protected abstract JExpression getFragmentId(Element element, String fieldName);

}
