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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.ref;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.ViewsById;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.FoundViewHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldRef;

public class ViewsByIdHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper helper;

	public ViewsByIdHandler(ProcessingEnvironment processingEnvironment) {
		super(ViewsById.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.isDeclaredType(element, valid);

		validatorHelper.extendsListOfView(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		JFieldRef elementRef = ref(element.getSimpleName().toString());

		TypeMirror viewType = extractViewClass(element);
		JClass viewClass = codeModelHelper.typeMirrorToJClass(viewType, holder);

		instantiateArrayList(elementRef, viewType, holder);
		clearList(elementRef, holder);

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(processHolder, element, IRClass.Res.ID, true);
		for (JFieldRef idRef : idsRefs) {
			addViewToListIfNotNull(elementRef, viewClass, idRef, holder);

		}
	}

	private void instantiateArrayList(JFieldRef elementRef, TypeMirror viewType, EComponentWithViewSupportHolder holder) {
		TypeElement arrayListTypeElement = helper.typeElementFromQualifiedName(CanonicalNameConstants.ARRAYLIST);
		DeclaredType arrayListType = helper.getTypeUtils().getDeclaredType(arrayListTypeElement, viewType);
		JClass arrayListClass = codeModelHelper.typeMirrorToJClass(arrayListType, holder);

		holder.getInitBody().assign(elementRef, _new(arrayListClass));
	}

	private TypeMirror extractViewClass(Element element) {
		DeclaredType elementType = (DeclaredType) element.asType();
		List<? extends TypeMirror> elementTypeArguments = elementType.getTypeArguments();

		TypeMirror viewType = helper.typeElementFromQualifiedName(CanonicalNameConstants.VIEW).asType();
		if (!elementTypeArguments.isEmpty()) {
			viewType = elementTypeArguments.get(0);
		}
		return viewType;
	}

	private void clearList(JFieldRef elementRef, EComponentWithViewSupportHolder holder) {
		holder.getOnViewChangedBodyBeforeFindViews().add(elementRef.invoke("clear"));
	}

	private void addViewToListIfNotNull(JFieldRef elementRef, JClass viewClass, JFieldRef idRef, EComponentWithViewSupportHolder holder) {
		FoundViewHolder foundViewHolder = holder.getFoundViewHolder(idRef, viewClass);
		foundViewHolder.getIfNotNullBlock().invoke(elementRef, "add").arg(foundViewHolder.getOrCastRef(viewClass));
	}

}
