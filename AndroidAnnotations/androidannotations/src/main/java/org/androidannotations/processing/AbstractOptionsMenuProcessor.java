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

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.ThirdPartyLibHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class AbstractOptionsMenuProcessor implements DecoratingElementProcessor {

	protected final ThirdPartyLibHelper libHelper;
	protected final IdAnnotationHelper annotationHelper;

	public AbstractOptionsMenuProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		libHelper = new ThirdPartyLibHelper(annotationHelper);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		if (holder.onCreateOptionMenuMethodBody == null) {
			Classes classes = holder.classes();

			boolean isFragment = holder.eBeanAnnotation == EFragment.class;

			JClass menuClass;
			JClass menuInflaterClass;
			String getMenuInflaterMethodName;
			if (libHelper.usesActionBarSherlock(holder)) {
				menuClass = classes.SHERLOCK_MENU;
				menuInflaterClass = classes.SHERLOCK_MENU_INFLATER;
				getMenuInflaterMethodName = "getSupportMenuInflater";
			} else {
				menuClass = classes.MENU;
				menuInflaterClass = classes.MENU_INFLATER;
				getMenuInflaterMethodName = "getMenuInflater";
			}

			JType returnType;
			if (isFragment) {
				returnType = codeModel.VOID;
			} else {
				returnType = codeModel.BOOLEAN;
			}

			JBlock body;
			JVar menuInflater;
			JVar menuParam;

			JMethod method = holder.generatedClass.method(PUBLIC, returnType, "onCreateOptionsMenu");
			method.annotate(Override.class);

			menuParam = method.param(menuClass, "menu");

			JBlock methodBody = method.body();

			if (isFragment) {
				menuInflater = method.param(menuInflaterClass, "inflater");
			} else {
				menuInflater = methodBody.decl(menuInflaterClass, "menuInflater", invoke(getMenuInflaterMethodName));
			}

			body = methodBody.block();

			JInvocation superCall = invoke(JExpr._super(), method);
			superCall.arg(menuParam);

			if (isFragment) {
				superCall.arg(menuInflater);
				methodBody.add(superCall);
			} else {
				methodBody._return(superCall);
			}

			if (isFragment) {
				holder.initBody.invoke("setHasOptionsMenu").arg(JExpr.TRUE);
			}

			holder.onCreateOptionMenuMethodBody = body;
			holder.onCreateOptionMenuMenuInflaterVariable = menuInflater;
			holder.onCreateOptionMenuMenuParam = menuParam;
		}
	}

}
