/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.BaseGeneratingAnnotationHandler;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.core.helper.CoreValidatorHelper;

public abstract class CoreBaseGeneratingAnnotationHandler<T extends GeneratedClassHolder> extends BaseGeneratingAnnotationHandler<T> {

	protected final CoreValidatorHelper coreValidatorHelper;

	public CoreBaseGeneratingAnnotationHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		this(targetClass.getCanonicalName(), environment);
	}

	public CoreBaseGeneratingAnnotationHandler(String target, AndroidAnnotationsEnvironment environment) {
		super(target, environment);
		coreValidatorHelper = new CoreValidatorHelper(annotationHelper);
	}
}
