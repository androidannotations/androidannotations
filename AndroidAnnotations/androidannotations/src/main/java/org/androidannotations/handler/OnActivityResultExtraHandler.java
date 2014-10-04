/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.holder.HasOnActivityResult;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class OnActivityResultExtraHandler extends BaseAnnotationHandler<HasOnActivityResult> {

	private final static APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public OnActivityResultExtraHandler(ProcessingEnvironment processingEnvironment) {
		super(OnActivityResult.Extra.class, processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingMethodHasAnnotation(OnActivityResult.class, element, validatedElements, valid);

		validatorHelper.canBePutInABundle(element, valid);
	}

	@Override
	public void process(Element element, HasOnActivityResult holder) throws Exception {
		// Don't do anything
	}

	public static JExpression getExtraValue(HasOnActivityResult holder, JBlock block, VariableElement parameter) {
		OnActivityResult.Extra annotation = parameter.getAnnotation(OnActivityResult.Extra.class);
		String parameterName = parameter.getSimpleName().toString();
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = parameterName;
		}

		JVar extras = holder.getOnActivityResultExtras();
		BundleHelper bundleHelper = new BundleHelper(new AnnotationHelper(holder.processingEnvironment()), parameter.asType());
		JExpression restoreMethodCall = JExpr.invoke(extras, bundleHelper.getMethodNameToRestore()).arg(extraKey);
		JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType(), holder);
		if (bundleHelper.restoreCallNeedCastStatement()) {
			restoreMethodCall = JExpr.cast(parameterClass, restoreMethodCall);
		}
		return block.decl(parameterClass, parameterName + "_", restoreMethodCall);
	}
}
