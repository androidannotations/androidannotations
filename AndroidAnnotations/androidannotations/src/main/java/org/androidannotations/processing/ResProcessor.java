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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class ResProcessor implements DecoratingElementProcessor {

	private final AndroidRes androidValue;

	private final IdAnnotationHelper annotationHelper;

	public ResProcessor(ProcessingEnvironment processingEnv, AndroidRes androidValue, IRClass rClass) {
		this.androidValue = androidValue;
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return androidValue.getTarget();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		Res resInnerClass = androidValue.getRInnerClass();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, resInnerClass, true);

		JBlock methodBody = holder.initBody;

		TypeMirror fieldTypeMirror = element.asType();
		String fieldType = fieldTypeMirror.toString();

		// Special case for loading animations
		if (CanonicalNameConstants.ANIMATION.equals(fieldType)) {
			methodBody.assign(ref(fieldName), classes.ANIMATION_UTILS.staticInvoke("loadAnimation").arg(holder.contextRef).arg(idRef));
		} else {
			if (holder.resources == null) {
				holder.resources = methodBody.decl(classes.RESOURCES, "resources_", holder.contextRef.invoke("getResources"));
			}

			String resourceMethodName = androidValue.getResourceMethodName();

			// Special case for @HtmlRes
			if (element.getAnnotation(HtmlRes.class) != null) {
				methodBody.assign(ref(fieldName), classes.HTML.staticInvoke("fromHtml").arg(invoke(holder.resources, resourceMethodName).arg(idRef)));
			} else {
				methodBody.assign(ref(fieldName), invoke(holder.resources, resourceMethodName).arg(idRef));
			}
		}

	}

}
