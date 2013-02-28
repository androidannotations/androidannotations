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

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.helper.ModelConstants;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class EApplicationProcessor implements GeneratingElementProcessor {

	public static final String GET_APPLICATION_INSTANCE = "getInstance";

	@Override
	public Class<? extends Annotation> getTarget() {
		return EApplication.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		TypeElement typeElement = (TypeElement) element;

		String annotatedComponentQualifiedName = typeElement.getQualifiedName().toString();

		String generatedComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;

		JDefinedClass generatedClass = codeModel._class(PUBLIC | FINAL, generatedComponentQualifiedName, ClassType.CLASS);
		EBeanHolder holder = eBeansHolder.create(element, getTarget(), generatedClass);

		JClass annotatedComponent = codeModel.directClass(annotatedComponentQualifiedName);

		holder.generatedClass._extends(annotatedComponent);

		holder.contextRef = _this();

		JFieldVar staticInstanceField = holder.generatedClass.field(PRIVATE | STATIC, annotatedComponent, "INSTANCE_");
		{
			// Static singleton getter and setter
			JMethod getInstance = holder.generatedClass.method(PUBLIC | JMod.STATIC, annotatedComponent, GET_APPLICATION_INSTANCE);
			getInstance.body()._return(staticInstanceField);

			JMethod setInstance = holder.generatedClass.method(PUBLIC | JMod.STATIC, codeModel.VOID, "setForTesting");
			setInstance.javadoc().append("Visible for testing purposes");
			JVar applicationParam = setInstance.param(annotatedComponent, "application");
			setInstance.body().assign(staticInstanceField, applicationParam);
		}

		holder.init = holder.generatedClass.method(PRIVATE, codeModel.VOID, "init_");
		{
			// onCreate
			JMethod onCreate = holder.generatedClass.method(PUBLIC, codeModel.VOID, "onCreate");
			onCreate.annotate(Override.class);
			JBlock onCreateBody = onCreate.body();
			onCreateBody.assign(staticInstanceField, _this());
			onCreateBody.invoke(holder.init);
			onCreateBody.invoke(JExpr._super(), onCreate);
		}

		{
			/*
			 * Setting to null shouldn't be a problem as long as we do not allow
			 * 
			 * @Extra on this component
			 */
			holder.initIfActivityBody = null;
			holder.initActivityRef = null;
		}

	}

}
