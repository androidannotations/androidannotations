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

import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.holder.HasInstanceState;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class InstanceStateHandler extends BaseAnnotationHandler<HasInstanceState> {

	public InstanceStateHandler(AndroidAnnotationsEnvironment environment) {
		super(InstanceState.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragmentOrEViewOrEViewGroup(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.canBePutInABundle(element, validation);
	}

	@Override
	public void process(Element element, HasInstanceState holder) {
		AbstractJClass elementClass = codeModelHelper.typeMirrorToJClass(element.asType());
		String fieldName = element.getSimpleName().toString();

		JBlock saveStateBody = holder.getSaveStateMethodBody();
		JVar saveStateBundleParam = holder.getSaveStateBundleParam();
		JMethod restoreStateMethod = holder.getRestoreStateMethod();
		JBlock restoreStateBody = restoreStateMethod.body();
		JVar restoreStateBundleParam = holder.getRestoreStateBundleParam();

		TypeMirror type = codeModelHelper.getActualType(element, holder);

		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), type);

		JFieldRef ref = ref(fieldName);
		saveStateBody.add(bundleHelper.getExpressionToSaveFromField(saveStateBundleParam, JExpr.lit(fieldName), ref));

		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, restoreStateBundleParam, JExpr.lit(fieldName), restoreStateMethod);
		restoreStateBody.assign(ref, restoreMethodCall);
	}
}
