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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr.ref;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EApplicationHolder;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;

public class AppHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AppHandler(AndroidAnnotationsEnvironment environment) {
		super(App.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.typeHasAnnotation(EApplication.class, element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();
		String applicationQualifiedName = element.asType().toString();
		AbstractJClass applicationClass = getJClass(applicationQualifiedName + classSuffix());

		holder.getInitBodyInjectionBlock().assign(ref(fieldName), applicationClass.staticInvoke(EApplicationHolder.GET_APPLICATION_INSTANCE));
	}
}
