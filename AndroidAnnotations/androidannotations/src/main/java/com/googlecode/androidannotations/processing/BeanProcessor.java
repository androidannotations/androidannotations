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

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.googlecode.androidannotations.processing.EBeanProcessor.GET_INSTANCE_METHOD_NAME;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Bean;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

public class BeanProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Bean.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();

		String typeQualifiedName = elementType.toString();

		JClass injectedClass = codeModel.ref(typeQualifiedName + GENERATION_SUFFIX);

		{
			// getInstance
			JBlock body = holder.init.body();

			JInvocation getInstance = injectedClass.staticInvoke(GET_INSTANCE_METHOD_NAME).arg(holder.contextRef);
			body.assign(ref(fieldName), getInstance);
		}

		{
			// afterSetContentView
			if (holder.afterSetContentView != null) {
				JBlock body = holder.afterSetContentView.body();

				body.invoke(cast(injectedClass, ref(fieldName)), holder.afterSetContentView);
			}
		}

	}

}
