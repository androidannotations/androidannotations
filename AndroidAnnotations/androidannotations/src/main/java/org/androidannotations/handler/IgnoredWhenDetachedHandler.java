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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.invoke;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;

public class IgnoredWhenDetachedHandler extends BaseAnnotationHandler<EFragmentHolder> {

	public IgnoredWhenDetachedHandler(ProcessingEnvironment processingEnvironment) {
		super(IgnoredWhenDetached.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.isNotPrivate(element, valid);
		validatorHelper.isNotFinal(element, valid);
		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);
		validatorHelper.enclosingElementHasEFragment(element, validatedElements, valid);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;
		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JBlock previousMethodBody = codeModelHelper.removeBody(delegatingMethod);

		delegatingMethod.body()._if(invoke(holder.getGeneratedClass().staticRef("this"), "getActivity").ne(_null()))._then().add(previousMethodBody);
	}
}
