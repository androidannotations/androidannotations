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

import static com.helger.jcodemodel.JExpr.invoke;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidRes;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JVar;

abstract class ContextCompatAwareResHandler extends AbstractResHandler {

	private final int minSdkWithMethod;
	private final String minSdkPlatformName;

	ContextCompatAwareResHandler(AndroidRes androidRes, AndroidAnnotationsEnvironment environment, int minSdkWithMethod, String minSdkPlatformName) {
		super(androidRes, environment);

		this.minSdkWithMethod = minSdkWithMethod;
		this.minSdkPlatformName = minSdkPlatformName;
	}

	@Override
	protected IJExpression getInstanceInvocation(EComponentHolder holder, JFieldRef idRef, IJAssignmentTarget fieldRef, JBlock targetBlock) {
		if (hasTargetMethodInAndroidxContextCompat()) {
			return getClasses().ANDROIDX_CONTEXT_COMPAT.staticInvoke(androidRes.getResourceMethodName()).arg(holder.getContextRef()).arg(idRef);
		} else if (hasTargetMethodInContextCompat()) {
			return getClasses().CONTEXT_COMPAT.staticInvoke(androidRes.getResourceMethodName()).arg(holder.getContextRef()).arg(idRef);
		} else if (shouldUseContextMethod()) {
			return holder.getContextRef().invoke(androidRes.getResourceMethodName()).arg(idRef);
		} else if (!shouldUseContextMethod() && hasTargetMethodInContext()) {
			return createCallWithIfGuard(holder, idRef, fieldRef, targetBlock);
		} else {
			return invoke(holder.getResourcesRef(), androidRes.getResourceMethodName()).arg(idRef);
		}
	}

	private boolean shouldUseContextMethod() {
		return getEnvironment().getAndroidManifest().getMinSdkVersion() >= minSdkWithMethod;
	}

	private boolean hasTargetMethodInContext() {
		TypeElement context = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT);

		return hasTargetMethod(context, androidRes.getResourceMethodName());
	}

	private boolean hasTargetMethodInContextCompat() {
		TypeElement contextCompat = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT_COMPAT);

		return hasTargetMethod(contextCompat, androidRes.getResourceMethodName());
	}

	private boolean hasTargetMethodInAndroidxContextCompat() {
		TypeElement contextCompat = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_CONTEXT_COMPAT);

		return hasTargetMethod(contextCompat, androidRes.getResourceMethodName());
	}

	private IJExpression createCallWithIfGuard(EComponentHolder holder, JFieldRef idRef, IJAssignmentTarget fieldRef, JBlock targetBlock) {
		JVar resourcesRef = holder.getResourcesRef();
		IJExpression buildVersionCondition = getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef(minSdkPlatformName));

		JConditional conditional = targetBlock._if(buildVersionCondition);
		conditional._then().add(fieldRef.assign(holder.getContextRef().invoke(androidRes.getResourceMethodName()).arg(idRef)));
		conditional._else().add(fieldRef.assign(resourcesRef.invoke(androidRes.getResourceMethodName()).arg(idRef)));

		return null;
	}
}
