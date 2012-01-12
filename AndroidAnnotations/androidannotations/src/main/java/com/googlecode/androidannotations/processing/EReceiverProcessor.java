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

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.EReceiver;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EReceiverProcessor extends AnnotationHelper implements ElementProcessor {

	public EReceiverProcessor(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EReceiver.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

		EBeanHolder holder = activitiesHolder.create(element);

		TypeElement typeElement = (TypeElement) element;

		String annotatedComponentQualifiedName = typeElement.getQualifiedName().toString();

		String generatedComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;

		holder.eBean = codeModel._class(PUBLIC | FINAL, generatedComponentQualifiedName, ClassType.CLASS);

		JClass annotatedComponent = codeModel.directClass(annotatedComponentQualifiedName);

		holder.eBean._extends(annotatedComponent);
		
		JClass contextClass = holder.refClass("android.content.Context");
		
		JFieldVar contextField = holder.eBean.field(PRIVATE, contextClass, "context_");
		holder.contextRef = contextField;

		holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
		{
			// onReceive
			JClass intentClass = holder.refClass("android.content.Intent");
			JMethod onReceive = holder.eBean.method(PUBLIC, codeModel.VOID, "onReceive");
			JVar contextParam = onReceive.param(contextClass, "context");
			JVar intentParam = onReceive.param(intentClass, "intent");
			onReceive.annotate(Override.class);
			JBlock onReceiveBody = onReceive.body();
			onReceiveBody.assign(contextField, contextParam);
			onReceiveBody.invoke(holder.init);
			onReceiveBody.invoke(JExpr._super(), onReceive).arg(contextParam).arg(intentParam);
		}

		{
			/*
			 * Setting to null shouldn't be a problem as long as we don't allow
			 * @App and @Extra on this component
			 */
			holder.initIfActivityBody = null;
			holder.initActivityRef = null;
		}

	}


}
