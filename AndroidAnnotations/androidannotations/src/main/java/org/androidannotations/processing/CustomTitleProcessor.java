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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.CustomTitle;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class CustomTitleProcessor implements DecoratingElementProcessor {
	private final AnnotationHelper annotationHelper;
	private final IRClass rClass;

	public CustomTitleProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new AnnotationHelper(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return CustomTitle.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		JFieldRef customTitleFeature = holder.classes().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");

		holder.initBody.invoke("requestWindowFeature").arg(customTitleFeature);
		JFieldRef contentViewId = annotationHelper.extractAnnotationFieldRefs(holder, element, CustomTitle.class, rClass.get(Res.LAYOUT), false).get(0);
		holder.onViewChanged().body().add(holder.contextRef.invoke("getWindow").invoke("setFeatureInt").arg(customTitleFeature).arg(contentViewId));
	}
}