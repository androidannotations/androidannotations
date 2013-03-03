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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.FromHtml;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class FromHtmlProcessor implements DecoratingElementProcessor {

	private final IdAnnotationHelper annotationHelper;

	public FromHtmlProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FromHtml.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws Exception {
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, Res.STRING, true);

		JBlock methodBody = holder.onViewChanged().body();

		//
		methodBody. //
				_if(ref(fieldName).ne(_null())). //
				_then() //
				.invoke(ref(fieldName), "setText").arg(classes.HTML.staticInvoke("fromHtml").arg(holder.contextRef.invoke("getString").arg(idRef)));
	}
}
