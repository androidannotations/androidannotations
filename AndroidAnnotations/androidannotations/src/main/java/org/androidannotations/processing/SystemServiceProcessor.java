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

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.SystemService;
import org.androidannotations.model.AndroidSystemServices;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class SystemServiceProcessor implements DecoratingElementProcessor {

	private final AndroidSystemServices androidSystemServices;

	public SystemServiceProcessor(AndroidSystemServices androidSystemServices) {
		this.androidSystemServices = androidSystemServices;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return SystemService.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror serviceType = element.asType();
		String fieldTypeQualifiedName = serviceType.toString();

		JFieldRef serviceRef = androidSystemServices.getServiceConstant(serviceType, holder);

		JBlock methodBody = holder.initBody;

		methodBody.assign(ref(fieldName), cast(holder.refClass(fieldTypeQualifiedName), holder.contextRef.invoke("getSystemService").arg(serviceRef)));
	}

}
