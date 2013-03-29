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
package org.androidannotations.rclass;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.helper.Option;

public class AndroidRClassFinder {

	private final ProcessingEnvironment processingEnv;

	public AndroidRClassFinder(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	public Option<IRClass> find() {
		TypeElement androidRType = processingEnv.getElementUtils().getTypeElement("android.R");
		if (androidRType == null) {
			Messager messager = processingEnv.getMessager();
			messager.printMessage(Kind.ERROR, "The android.R class cannot be found");
			return Option.absent();
		}
		return Option.<IRClass> of(new RClass(androidRType));
	}
}
