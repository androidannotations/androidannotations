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
package org.androidannotations.plugin;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

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

	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		return holder.definedClass(fullyQualifiedClassName);
	}

	public ProcessHolder.Classes classes() {
		return holder.classes();
	}

	public JClass refClass(String fullyQualifiedClassName) {
		return holder.refClass(fullyQualifiedClassName);
	}

	public JClass refClass(Class<?> clazz) {
		return holder.refClass(clazz);
	}

	public JCodeModel codeModel() {
		return holder.codeModel();
	}

	public ProcessingEnvironment processingEnvironment() {
		return holder.processingEnvironment();
	}

	public TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	public AndroidAnnotationsEnvironment environment() {
		return holder().environment();
	}
}
