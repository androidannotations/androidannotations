/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.res.HtmlRes;
import com.googlecode.androidannotations.model.AndroidRes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
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
		if ("android.view.animation.Animation".equals(fieldType)) {
			JClass animationUtils = holder.refClass("android.view.animation.AnimationUtils");
			methodBody.assign(JExpr.ref(fieldName), animationUtils.staticInvoke("loadAnimation").arg(JExpr._this()).arg(idRef));
		} else {
			if (holder.resources == null)
				holder.resources = methodBody.decl(holder.refClass("android.content.res.Resources"), "resources_", holder.contextRef.invoke("getResources"));

			String resourceMethodName = androidValue.getResourceMethodName();

			// Special case for @HtmlRes
			if (element.getAnnotation(HtmlRes.class) != null) {
				JClass html = holder.refClass("android.text.Html");
				methodBody.assign(JExpr.ref(fieldName), html.staticInvoke("fromHtml").arg(JExpr.invoke(holder.resources, resourceMethodName).arg(idRef)));
			} else {
				methodBody.assign(JExpr.ref(fieldName), JExpr.invoke(holder.resources, resourceMethodName).arg(idRef));
			}
		}

	}

}
