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

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.APTCodeModelHelper.Parameter;
import org.androidannotations.helper.HasTarget;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JTypeVar;

public abstract class GeneratingElementProcessor implements HasTarget {

	protected final APTCodeModelHelper helper = new APTCodeModelHelper();

	public int getGeneratedClassModifiers(Element element) {
		return PUBLIC | FINAL;
	}

	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {
		TypeElement typeElement = (TypeElement) element;

		// Retrieve annotated and generated qualified names
		String annotatedQualifiedName = typeElement.getQualifiedName().toString();
		String generatedQualifiedName = annotatedQualifiedName + GENERATION_SUFFIX;

		// Create instance of theses two classes
		JDefinedClass generatedClass = codeModel._class(getGeneratedClassModifiers(element), generatedQualifiedName, ClassType.CLASS);
		JClass annotatedClass = codeModel.directClass(annotatedQualifiedName);

		// Create the EBeanHolder instance for generated class
		EBeanHolder holder = eBeansHolder.create(element, getTarget(), generatedClass);

		// Handle generics
		{
			holder.typedParameters = helper.extractTypedParameters(typeElement.asType(), holder);

			for (Parameter typedParameter : holder.typedParameters) {
				JTypeVar typeVar = holder.generatedClass.generify(typedParameter.name, typedParameter.jClass);
				annotatedClass = annotatedClass.narrow(typeVar);
			}
		}

		// Bound generated class with the annotated one
		generatedClass._extends(annotatedClass);

		process(typeElement, codeModel, eBeansHolder, holder);
	}

	abstract void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder, EBeanHolder holder) throws Exception;

}
