/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.GeneratedClassHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;

public class PrefHandler extends CoreBaseAnnotationHandler<EComponentHolder> implements MethodInjectionHandler<EComponentHolder> {

	private final InjectHelper<EComponentHolder> injectHelper;

	public PrefHandler(AndroidAnnotationsEnvironment environment) {
		super(Pref.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(Pref.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		validatorHelper.isNotPrivate(element, validation);

		coreValidatorHelper.isSharedPreference(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentHolder holder, Element element, Element param) {
		TypeMirror fieldTypeMirror = param.asType();
		AbstractJClass prefClass = getJClass(fieldTypeMirror.toString());

		String elementTypeName = fieldTypeMirror.toString();
		int index = elementTypeName.lastIndexOf(".");
		if (index != -1) {
			elementTypeName = elementTypeName.substring(index + 1);
		}

		Set<? extends Element> sharedPrefElements = getEnvironment().getValidatedElements().getRootAnnotatedElements(SharedPref.class.getName());
		for (Element sharedPrefElement : sharedPrefElements) {
			GeneratedClassHolder sharedPrefHolder = getEnvironment().getGeneratedClassHolder(sharedPrefElement);
			String sharedPrefName = sharedPrefHolder.getGeneratedClass().name();

			if (elementTypeName.equals(sharedPrefName)) {
				prefClass = sharedPrefHolder.getGeneratedClass();
				break;
			}
		}

		targetBlock.add(fieldRef.assign(JExpr._new(prefClass).arg(holder.getContextRef())));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, valid);
	}
}
