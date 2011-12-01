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
package com.googlecode.androidannotations.rclass;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import com.googlecode.androidannotations.helper.AndroidManifest;
import com.googlecode.androidannotations.helper.AnnotationHelper;

public class ProjectRClassFinder extends AnnotationHelper {

	public ProjectRClassFinder(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	public IRClass find(AndroidManifest manifest) {

		Elements elementUtils = processingEnv.getElementUtils();
		String rClass = manifest.getApplicationPackage() + ".R";
		TypeElement rType = elementUtils.getTypeElement(rClass);

		if (rType == null) {
			Messager messager = processingEnv.getMessager();
			messager.printMessage(Kind.WARNING, "The AndroidManifest.xml file was found, but not the compiled R class: " + rClass);
			return IRClass.EMPTY_R_CLASS;
		}

		return new RClass(rType);
	}

}
