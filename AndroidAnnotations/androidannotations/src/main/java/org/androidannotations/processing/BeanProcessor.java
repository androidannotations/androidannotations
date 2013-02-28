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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;
import static org.androidannotations.processing.EBeanProcessor.GET_INSTANCE_METHOD_NAME;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.TargetAnnotationHelper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;

public class BeanProcessor implements DecoratingElementProcessor {

	private TargetAnnotationHelper annotationHelper;
	protected final APTCodeModelHelper helper;

	public BeanProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		helper = new APTCodeModelHelper();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Bean.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		DeclaredType targetAnnotationClassValue = annotationHelper.extractAnnotationClassParameter(element);

		TypeMirror elementType;
		if (targetAnnotationClassValue != null) {
			elementType = targetAnnotationClassValue;
		} else {
			elementType = element.asType();
		}

		JClass generatedClass = helper.getGeneratedClass(elementType, holder);
		JClass rawInjectedClass = generatedClass.erasure();

		String fieldName = element.getSimpleName().toString();
		JFieldRef beanField = ref(fieldName);
		{
			// getInstance
			JBlock body = holder.init.body();

			boolean hasNonConfigurationInstanceAnnotation = element.getAnnotation(NonConfigurationInstance.class) != null;

			if (hasNonConfigurationInstanceAnnotation) {
				body = body._if(beanField.eq(_null()))._then();
			}

			JInvocation getInstance = rawInjectedClass.staticInvoke(GET_INSTANCE_METHOD_NAME).arg(holder.contextRef);
			body.assign(beanField, getInstance);
		}

		{
			// afterSetContentView
			if (holder.afterSetContentView != null) {
				JBlock body = holder.afterSetContentView.body();

				body.invoke(cast(generatedClass, beanField), holder.afterSetContentView);
			}
		}

	}
}
