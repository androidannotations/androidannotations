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

import static com.sun.codemodel.JExpr.ref;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.holder.EApplicationHolder;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JClass;

public class AppHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AppHandler(ProcessingEnvironment processingEnvironment) {
		super(App.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.typeHasAnnotation(EApplication.class, element, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();
		String applicationQualifiedName = element.asType().toString();
		JClass applicationClass = refClass(applicationQualifiedName + classSuffix());

		holder.getInitBody().assign(ref(fieldName), applicationClass.staticInvoke(EApplicationHolder.GET_APPLICATION_INSTANCE));
	}
}
