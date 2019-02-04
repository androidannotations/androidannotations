/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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
package org.androidannotations.plugin;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

public class PluginClassHolder<H extends GeneratedClassHolder> {

	private H holder;

	public PluginClassHolder(H holder) {
		this.holder = holder;
	}

	public H holder() {
		return holder;
	}

	public JDefinedClass getGeneratedClass() {
		return holder.getGeneratedClass();
	}

	public TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	public AndroidAnnotationsEnvironment environment() {
		return holder().getEnvironment();
	}

	protected AbstractJClass getJClass(String fullyQualifiedClassName) {
		return environment().getJClass(fullyQualifiedClassName);
	}

	protected AbstractJClass getJClass(Class<?> clazz) {
		return environment().getJClass(clazz);
	}

	protected JCodeModel getCodeModel() {
		return environment().getCodeModel();
	}

	protected ProcessingEnvironment getProcessingEnvironment() {
		return environment().getProcessingEnvironment();
	}

	protected ProcessHolder.Classes getClasses() {
		return environment().getClasses();
	}

}
