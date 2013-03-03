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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.helper.ModelConstants;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class RestServiceProcessor implements DecoratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return RestService.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror fieldTypeMirror = element.asType();
		String interfaceName = fieldTypeMirror.toString();

		String generatedClassName = interfaceName + ModelConstants.GENERATION_SUFFIX;

		JBlock methodBody = holder.initBody;

		JFieldRef field = JExpr.ref(fieldName);

		methodBody.assign(field, JExpr._new(holder.refClass(generatedClassName)));
	}

}
