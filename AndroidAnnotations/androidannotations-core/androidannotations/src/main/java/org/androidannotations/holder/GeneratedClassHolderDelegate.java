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
package org.androidannotations.holder;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.process.ProcessHolder.Classes;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public abstract class GeneratedClassHolderDelegate<T extends GeneratedClassHolder> implements GeneratedClassHolder {

	protected T holder;

	public GeneratedClassHolderDelegate(T holder) {
		this.holder = holder;
	}

	@Override
	public final JDefinedClass getGeneratedClass() {
		return holder.getGeneratedClass();
	}

	@Override
	public final TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	@Override
	public AndroidAnnotationsEnvironment environment() {
		return holder.environment();
	}

	protected final Classes getClasses() {
		return environment().getClasses();
	}

	protected final JCodeModel codeModel() {
		return environment().getCodeModel();
	}

	protected final JClass refClass(String fullyQualifiedClassName) {
		return environment().getJClass(fullyQualifiedClassName);
	}

	protected final JClass refClass(Class<?> clazz) {
		return environment().getJClass(clazz);
	}
}
