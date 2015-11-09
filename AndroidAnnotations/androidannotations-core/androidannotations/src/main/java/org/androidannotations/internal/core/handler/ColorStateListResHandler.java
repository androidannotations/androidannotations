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

public class ColorStateListResHandler extends AbstractResHandler {

	private static final int MIN_SDK_WITH_CONTEXT_GET_COLOR_STATE_LIST = 23;

	public ColorStateListResHandler(AndroidAnnotationsEnvironment environment) {
		super(AndroidRes.COLOR_STATE_LIST, environment);
	}

	@Override
	protected void makeCall(String fieldName, EComponentHolder holder, JBlock methodBody, JFieldRef idRef) {
		JFieldRef ref = ref(fieldName);
		if (hasContextCompatInClasspath()) {
			methodBody.assign(ref, getClasses().CONTEXT_COMPAT.staticInvoke("getColorStateList").arg(holder.getContextRef()).arg(idRef));
		} else if (shouldUseContextGetColorStateListMethod() && !hasContextCompatInClasspath()) {
			methodBody.assign(ref, holder.getContextRef().invoke("getColorStateList").arg(idRef));
		} else if (!shouldUseContextGetColorStateListMethod() && hasGetColorStateListInContext() && !hasContextCompatInClasspath()) {
			createCallWithIfGuard(holder, ref, methodBody, idRef);
		} else {
			methodBody.assign(ref, invoke(holder.getResourcesRef(), androidRes.getResourceMethodName()).arg(idRef));
		}
	}

	private boolean hasContextCompatInClasspath() {
		return getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT_COMPAT) != null;
	}

	private boolean shouldUseContextGetColorStateListMethod() {
		return getEnvironment().getAndroidManifest().getMinSdkVersion() >= MIN_SDK_WITH_CONTEXT_GET_COLOR_STATE_LIST;
	}

	private boolean hasGetColorStateListInContext() {
		TypeElement context = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT);

		return hasGetColorStateList(context);
	}

	private void createCallWithIfGuard(EComponentHolder holder, JFieldRef ref, JBlock methodBody, JFieldRef idRef) {
		JVar resourcesRef = holder.getResourcesRef();
		JConditional guardIf = methodBody._if(getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef("M")));
		JBlock ifBlock = guardIf._then();
		ifBlock.assign(ref, holder.getContextRef().invoke("getColorStateList").arg(idRef));

		JBlock elseBlock = guardIf._else();
		elseBlock.assign(ref, resourcesRef.invoke("getColorStateList").arg(idRef));
	}

	private boolean hasGetColorStateList(TypeElement type) {
		if (type == null) {
			return false;
		}

		List<? extends Element> allMembers = getProcessingEnvironment().getElementUtils().getAllMembers(type);
		for (ExecutableElement element : ElementFilter.methodsIn(allMembers)) {
			if (element.getSimpleName().contentEquals("getColorStateList")) {
				return true;
			}
		}
		return false;
	}
}
