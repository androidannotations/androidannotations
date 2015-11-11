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

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;

public class BeanHandler extends BaseAnnotationHandler<EComponentHolder> {

	public BeanHandler(AndroidAnnotationsEnvironment environment) {
		super(Bean.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.typeOrTargetValueHasAnnotation(EBean.class, element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		TypeMirror typeMirror = annotationHelper.extractAnnotationClassParameter(element);
		if (typeMirror == null) {
			typeMirror = element.asType();
			typeMirror = getProcessingEnvironment().getTypeUtils().erasure(typeMirror);
		}

		String typeQualifiedName = typeMirror.toString();
		AbstractJClass injectedClass = getJClass(annotationHelper.generatedClassQualifiedNameFromQualifiedName(typeQualifiedName));

		String fieldName = element.getSimpleName().toString();
		JFieldRef beanField = ref(fieldName);
		JBlock block = holder.getInitBodyInjectionBlock();

		boolean hasNonConfigurationInstanceAnnotation = element.getAnnotation(NonConfigurationInstance.class) != null;
		if (hasNonConfigurationInstanceAnnotation) {
			block = block._if(beanField.eq(_null()))._then();
		}

		JInvocation getInstance = injectedClass.staticInvoke(EBeanHolder.GET_INSTANCE_METHOD_NAME).arg(holder.getContextRef());
		block.assign(beanField, getInstance);
	}
}
