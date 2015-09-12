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
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.internal.process.ProcessHolder.Classes;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

public abstract class GeneratedClassHolderDelegate<T extends GeneratedClassHolder> implements GeneratedClassHolder {

	protected T holder;

	protected APTCodeModelHelper codeModelHelper;

	public GeneratedClassHolderDelegate(T holder) {
		this.holder = holder;
		codeModelHelper = new APTCodeModelHelper(holder.getEnvironment());
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
	public AndroidAnnotationsEnvironment getEnvironment() {
		return holder.getEnvironment();
	}

	protected final Classes getClasses() {
		return getEnvironment().getClasses();
	}

	protected final JCodeModel codeModel() {
		return getEnvironment().getCodeModel();
	}

	protected final AbstractJClass refClass(String fullyQualifiedClassName) {
		return getEnvironment().getJClass(fullyQualifiedClassName);
	}

	protected final AbstractJClass refClass(Class<?> clazz) {
		return getEnvironment().getJClass(clazz);
	}
}
