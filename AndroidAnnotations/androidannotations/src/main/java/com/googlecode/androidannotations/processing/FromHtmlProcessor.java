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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class FromHtmlProcessor implements ElementProcessor {

	private final IRClass rClass;

	public FromHtmlProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FromHtml.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {
		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		FromHtml annotation = element.getAnnotation(FromHtml.class);
		int idValue = annotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.STRING);
		JFieldRef idRef;
		if (idValue == Id.DEFAULT_VALUE) {
			idRef = rInnerClass.getIdStaticRef(fieldName, holder);
		} else {
			idRef = rInnerClass.getIdStaticRef(idValue, holder);
		}

		JBlock methodBody = holder.afterSetContentView.body();

		//
		methodBody. //
				_if(ref(fieldName).ne(_null())). //
				_then() //
				.invoke(ref(fieldName), "setText").arg(classes.HTML.staticInvoke("fromHtml").arg(holder.contextRef.invoke("getString").arg(idRef)));
	}
}
