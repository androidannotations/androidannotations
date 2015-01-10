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

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;

public class ResHandler extends BaseAnnotationHandler<EComponentHolder> {

	private AndroidRes androidRes;
	private IdAnnotationHelper annotationHelper;

	public ResHandler(AndroidRes androidRes, ProcessingEnvironment processingEnvironment) {
		super(androidRes.getAnnotationClass(), processingEnvironment);
		this.androidRes = androidRes;
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		TypeMirror fieldTypeMirror = element.asType();

		validatorHelper.allowedType(element, valid, fieldTypeMirror, androidRes.getAllowedTypes());

		validatorHelper.resIdsExist(element, androidRes.getRInnerClass(), IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		IRClass.Res resInnerClass = androidRes.getRInnerClass();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(processHolder, element, resInnerClass, true);

		JBlock methodBody = holder.getInitBody();

		TypeMirror fieldTypeMirror = element.asType();
		String fieldType = fieldTypeMirror.toString();

		// Special case for loading animations
		if (CanonicalNameConstants.ANIMATION.equals(fieldType)) {
			methodBody.assign(ref(fieldName), classes().ANIMATION_UTILS.staticInvoke("loadAnimation").arg(holder.getContextRef()).arg(idRef));
		} else {
			String resourceMethodName = androidRes.getResourceMethodName();

			// Special case for @HtmlRes
			if (element.getAnnotation(HtmlRes.class) != null) {
				methodBody.assign(ref(fieldName), classes().HTML.staticInvoke("fromHtml").arg(invoke(holder.getResourcesRef(), resourceMethodName).arg(idRef)));
			} else {
				methodBody.assign(ref(fieldName), invoke(holder.getResourcesRef(), resourceMethodName).arg(idRef));
			}
		}
	}
}
