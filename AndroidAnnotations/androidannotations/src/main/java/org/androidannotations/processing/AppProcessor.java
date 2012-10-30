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
package org.androidannotations.processing;

import static org.androidannotations.helper.CanonicalNameConstants.APPLICATION;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.App;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

public class AppProcessor implements DecoratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return App.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();

		JInvocation getApplication = holder.initActivityRef.invoke("getApplication");

		String applicationTypeQualifiedName = elementType.toString();
		if (APPLICATION.equals(applicationTypeQualifiedName)) {
			holder.initIfActivityBody.assign(ref(fieldName), getApplication);
		} else {
			holder.initIfActivityBody.assign(ref(fieldName), cast(holder.refClass(applicationTypeQualifiedName), getApplication));
		}

	}

}
