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

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EProvider;
import org.androidannotations.helper.ModelConstants;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

public class EProviderProcessor implements GeneratingElementProcessor {

	@Override
	public String getTarget() {
		return EProvider.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		TypeElement typeElement = (TypeElement) element;

		String annotatedComponentQualifiedName = typeElement.getQualifiedName().toString();

		String generatedComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;

		JDefinedClass generatedClass = codeModel._class(PUBLIC | FINAL, generatedComponentQualifiedName, ClassType.CLASS);

		EBeanHolder holder = eBeansHolder.create(element, EProvider.class, generatedClass);

		JClass annotatedComponent = codeModel.directClass(annotatedComponentQualifiedName);

		holder.generatedClass._extends(annotatedComponent);

		holder.contextRef = invoke("getContext");

		JMethod init = holder.generatedClass.method(PRIVATE, codeModel.VOID, "init_");
		holder.initBody = init.body();
		{
			// onCreate
			JMethod onCreate = holder.generatedClass.method(PUBLIC, codeModel.BOOLEAN, "onCreate");
			onCreate.annotate(Override.class);
			JBlock onCreateBody = onCreate.body();
			onCreateBody.invoke(init);
			onCreateBody._return(invoke(_super(), onCreate));
		}

		{
			/*
			 * Setting to null shouldn't be a problem as long as we don't allow
			 * 
			 * @App and @Extra on this component
			 */
			holder.initIfActivityBody = null;
			holder.initActivityRef = null;
		}

	}

}
