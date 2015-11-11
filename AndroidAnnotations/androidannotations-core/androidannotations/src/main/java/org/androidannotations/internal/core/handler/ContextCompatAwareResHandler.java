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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JExpr.ref;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidRes;

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
	protected void makeCall(String fieldName, EComponentHolder holder, JBlock methodBody, JFieldRef idRef) {
		JFieldRef ref = ref(fieldName);
		if (hasContextCompatInClasspath()) {
			methodBody.assign(ref, getClasses().CONTEXT_COMPAT.staticInvoke(androidRes.getResourceMethodName()).arg(holder.getContextRef()).arg(idRef));
		} else if (shouldUseContextMethod() && !hasContextCompatInClasspath()) {
			methodBody.assign(ref, holder.getContextRef().invoke(androidRes.getResourceMethodName()).arg(idRef));
		} else if (!shouldUseContextMethod() && hasTargetMethodInContext() && !hasContextCompatInClasspath()) {
			createCallWithIfGuard(holder, ref, methodBody, idRef);
		} else {
			methodBody.assign(ref, invoke(holder.getResourcesRef(), androidRes.getResourceMethodName()).arg(idRef));
		}
	}

	private boolean shouldUseContextMethod() {
		return getEnvironment().getAndroidManifest().getMinSdkVersion() >= minSdkWithMethod;
	}

	private boolean hasTargetMethodInContext() {
		TypeElement context = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT);

		return hasTargetMethod(context);
	}

	private void createCallWithIfGuard(EComponentHolder holder, JFieldRef ref, JBlock methodBody, JFieldRef idRef) {
		JVar resourcesRef = holder.getResourcesRef();
		JConditional guardIf = methodBody._if(getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef(minSdkPlatformName)));
		JBlock ifBlock = guardIf._then();
		ifBlock.assign(ref, holder.getContextRef().invoke(androidRes.getResourceMethodName()).arg(idRef));

		JBlock elseBlock = guardIf._else();
		elseBlock.assign(ref, resourcesRef.invoke(androidRes.getResourceMethodName()).arg(idRef));
	}

	private boolean hasTargetMethod(TypeElement type) {
		if (type == null) {
			return false;
		}

		List<? extends Element> allMembers = getProcessingEnvironment().getElementUtils().getAllMembers(type);
		for (ExecutableElement element : ElementFilter.methodsIn(allMembers)) {
			if (element.getSimpleName().contentEquals(androidRes.getResourceMethodName())) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasContextCompatInClasspath() {
		return getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT_COMPAT) != null;
	}
}
