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
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr.TRUE;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.helper.SherlockHelper;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class OptionsMenuProcessor implements DecoratingElementProcessor {

	private final SherlockHelper sherlockHelper;

	private IdAnnotationHelper annotationHelper;

	public OptionsMenuProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		sherlockHelper = new SherlockHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OptionsMenu.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		boolean isFragment = holder.eBeanAnnotation == EFragment.class;

		JClass menuClass;
		JClass menuInflaterClass;
		String getMenuInflaterMethodName;
		if (sherlockHelper.usesSherlock(holder)) {
			menuClass = classes.SHERLOCK_MENU;
			menuInflaterClass = classes.SHERLOCK_MENU_INFLATER;
			getMenuInflaterMethodName = "getSupportMenuInflater";
		} else {
			menuClass = classes.MENU;
			menuInflaterClass = classes.MENU_INFLATER;
			getMenuInflaterMethodName = "getMenuInflater";
		}

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, Res.MENU, false);

		JType returnType;
		if (isFragment) {
			returnType = codeModel.VOID;
		} else {
			returnType = codeModel.BOOLEAN;
		}

		JMethod method = holder.generatedClass.method(PUBLIC, returnType, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JVar menuParam = method.param(menuClass, "menu");

		JBlock body = method.body();

		JVar menuInflater;
		if (isFragment) {
			menuInflater = method.param(menuInflaterClass, "inflater");
		} else {
			menuInflater = body.decl(menuInflaterClass, "menuInflater", invoke(getMenuInflaterMethodName));
		}

		for (JFieldRef optionsMenuRefId : fieldRefs) {
			body.invoke(menuInflater, "inflate").arg(optionsMenuRefId).arg(menuParam);
		}

		JInvocation superCall = invoke(_super(), method).arg(menuParam);
		if (isFragment) {
			superCall.arg(menuInflater);
			body.add(superCall);
		} else {
			body._return(superCall);
		}

		if (isFragment) {
			holder.init.body().invoke("setHasOptionsMenu").arg(TRUE);
		}
	}
}
