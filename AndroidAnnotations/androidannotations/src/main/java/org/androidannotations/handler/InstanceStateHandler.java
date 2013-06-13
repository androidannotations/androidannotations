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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.InstanceState;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.holder.HasInstanceState;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class InstanceStateHandler extends BaseAnnotationHandler<HasInstanceState> {

	public InstanceStateHandler(ProcessingEnvironment processingEnvironment) {
		super(InstanceState.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.canBeSavedAsInstanceState(element, valid);
	}

	@Override
	public void process(Element element, HasInstanceState holder) {
		String fieldName = element.getSimpleName().toString();

		JBlock saveStateBody = holder.getSaveStateMethodBody();
		JVar saveStateBundleParam = holder.getSaveStateBundleParam();
		JMethod restoreStateMethod = holder.getRestoreStateMethod();
		JBlock restoreStateBody = restoreStateMethod.body();
		JVar restoreStateBundleParam = holder.getRestoreStateBundleParam();

		AnnotationHelper annotationHelper = new AnnotationHelper(processingEnv);
		BundleHelper bundleHelper = new BundleHelper(annotationHelper, element);
		APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

		JFieldRef ref = ref(fieldName);
		saveStateBody.invoke(saveStateBundleParam, bundleHelper.getMethodNameToSave()).arg(fieldName).arg(ref);

		JInvocation restoreMethodCall = JExpr.invoke(restoreStateBundleParam, bundleHelper.getMethodNameToRestore()).arg(fieldName);
		if (bundleHelper.restoreCallNeedCastStatement()) {

			JClass jclass = codeModelHelper.typeMirrorToJClass(element.asType(), holder);
			JExpression castStatement = JExpr.cast(jclass, restoreMethodCall);
			restoreStateBody.assign(ref, castStatement);

			if (bundleHelper.restoreCallNeedsSuppressWarning()) {
				if (restoreStateMethod.annotations().size() == 0) {
					restoreStateMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
				}
			}

		} else {
			restoreStateBody.assign(ref, restoreMethodCall);
		}
	}
}
