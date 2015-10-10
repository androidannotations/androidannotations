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

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr.ref;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.ViewsById;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.FoundViewHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JFieldRef;

public class ViewsByIdHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public ViewsByIdHandler(AndroidAnnotationsEnvironment environment) {
		super(ViewsById.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.isDeclaredType(element, validation);

		validatorHelper.extendsListOfView(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		JFieldRef elementRef = ref(element.getSimpleName().toString());

		TypeMirror viewType = extractViewClass(element);
		AbstractJClass viewClass = codeModelHelper.typeMirrorToJClass(viewType);

		instantiateArrayList(elementRef, viewType, holder);
		clearList(elementRef, holder);

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);
		for (JFieldRef idRef : idsRefs) {
			addViewToListIfNotNull(elementRef, viewClass, idRef, holder);

		}
	}

	private void instantiateArrayList(JFieldRef elementRef, TypeMirror viewType, EComponentWithViewSupportHolder holder) {
		TypeElement arrayListTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.ARRAYLIST);
		DeclaredType arrayListType = getProcessingEnvironment().getTypeUtils().getDeclaredType(arrayListTypeElement, viewType);
		AbstractJClass arrayListClass = codeModelHelper.typeMirrorToJClass(arrayListType);

		holder.getInitBodyInjectionBlock().assign(elementRef, _new(arrayListClass));
	}

	private TypeMirror extractViewClass(Element element) {
		DeclaredType elementType = (DeclaredType) element.asType();
		List<? extends TypeMirror> elementTypeArguments = elementType.getTypeArguments();

		TypeMirror viewType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.VIEW).asType();
		if (!elementTypeArguments.isEmpty()) {
			viewType = elementTypeArguments.get(0);
		}
		return viewType;
	}

	private void clearList(JFieldRef elementRef, EComponentWithViewSupportHolder holder) {
		holder.getOnViewChangedBodyBeforeInjectionBlock().add(elementRef.invoke("clear"));
	}

	private void addViewToListIfNotNull(JFieldRef elementRef, AbstractJClass viewClass, JFieldRef idRef, EComponentWithViewSupportHolder holder) {
		FoundViewHolder foundViewHolder = holder.getFoundViewHolder(idRef, viewClass);
		foundViewHolder.getIfNotNullBlock().invoke(elementRef, "add").arg(foundViewHolder.getOrCastRef(viewClass));
	}

}
