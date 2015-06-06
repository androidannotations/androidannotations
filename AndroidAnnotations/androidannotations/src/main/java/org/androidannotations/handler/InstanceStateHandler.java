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

import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.InstanceState;
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
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class InstanceStateHandler extends BaseAnnotationHandler<HasInstanceState> {

	private AnnotationHelper annotationHelper;

	public InstanceStateHandler(ProcessingEnvironment processingEnvironment) {
		super(InstanceState.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.canBePutInABundle(element, valid);
	}

	@Override
	public void process(Element element, HasInstanceState holder) {
		JClass elementClass = codeModelHelper.typeMirrorToJClass(element.asType(), holder);
		String fieldName = element.getSimpleName().toString();

		JBlock saveStateBody = holder.getSaveStateMethodBody();
		JVar saveStateBundleParam = holder.getSaveStateBundleParam();
		JMethod restoreStateMethod = holder.getRestoreStateMethod();
		JBlock restoreStateBody = restoreStateMethod.body();
		JVar restoreStateBundleParam = holder.getRestoreStateBundleParam();

		TypeMirror type = codeModelHelper.getActualType(element, holder);

		BundleHelper bundleHelper = new BundleHelper(annotationHelper, type);

		JFieldRef ref = ref(fieldName);
		saveStateBody.invoke(saveStateBundleParam, bundleHelper.getMethodNameToSave()).arg(fieldName).arg(ref);

		JExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, restoreStateBundleParam, JExpr.lit(fieldName), restoreStateMethod, holder);
		restoreStateBody.assign(ref, restoreMethodCall);
	}
}
