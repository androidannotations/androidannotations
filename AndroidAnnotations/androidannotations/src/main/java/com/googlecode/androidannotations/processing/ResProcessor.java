/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.res.HtmlRes;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.model.AndroidRes;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class ResProcessor implements ElementProcessor {

	private final IRClass rClass;
	private final AndroidRes androidValue;

	public ResProcessor(AndroidRes androidValue, IRClass rClass) {
		this.rClass = rClass;
		this.androidValue = androidValue;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return androidValue.getTarget();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		int idValue = androidValue.idFromElement(element);

		Res resInnerClass = androidValue.getRInnerClass();

		IRInnerClass rInnerClass = rClass.get(resInnerClass);
		JFieldRef idRef;
		if (idValue == Id.DEFAULT_VALUE) {
			idRef = rInnerClass.getIdStaticRef(fieldName, holder);
		} else {
			idRef = rInnerClass.getIdStaticRef(idValue, holder);
		}

		JBlock methodBody = holder.init.body();

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
