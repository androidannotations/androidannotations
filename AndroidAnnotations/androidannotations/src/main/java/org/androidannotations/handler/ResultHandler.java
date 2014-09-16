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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.Result;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.helper.ValidatorHelper;
import org.androidannotations.holder.HasOnActivityResult;
import org.androidannotations.model.AnnotationElements;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.androidannotations.process.IsValid;

public class ResultHandler {

	private final static APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public static JExpression getExtraValue(HasOnActivityResult holder, JBlock block,
	        VariableElement parameter) {
		Result annotation = parameter.getAnnotation(Result.class);
		String parameterName = parameter.getSimpleName().toString();
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = parameterName;
		}

		JVar extras = holder.getOnActivityResultExtras();
		BundleHelper bundleHelper = new BundleHelper(new AnnotationHelper(holder.processingEnvironment()), parameter);
		JExpression restoreMethodCall = JExpr.invoke(extras, bundleHelper.getMethodNameToRestore()).arg(extraKey);
		JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType(), holder);
		if (bundleHelper.restoreCallNeedCastStatement()) {
			restoreMethodCall = JExpr.cast(parameterClass, restoreMethodCall);
		}
		return block.decl(parameterClass, parameterName + "_", restoreMethodCall);
	}
}
