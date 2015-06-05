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

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AndroidRes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;

public class DrawableResHandler extends AbstractResHandler {

	private static final int MIN_SDK_WITH_CONTEXT_GET_DRAWABLE = 21;

	public DrawableResHandler(ProcessingEnvironment processingEnvironment) {
		super(AndroidRes.DRAWABLE, processingEnvironment);
	}

	@Override
	protected void makeCall(String fieldName, EComponentHolder holder, JBlock methodBody, JFieldRef idRef) {
		JFieldRef ref = ref(fieldName);
		if (hasContextCompatInClasspath()) {
			methodBody.assign(ref, classes().CONTEXT_COMPAT.staticInvoke("getDrawable").arg(holder.getContextRef()).arg(idRef));
		} else if (shouldUseContextGetDrawableMethod() && !hasContextCompatInClasspath()) {
			methodBody.assign(ref, holder.getContextRef().invoke("getDrawable").arg(idRef));
		} else if (!shouldUseContextGetDrawableMethod() && hasGetDrawableInContext() && !hasContextCompatInClasspath()) {
			createCallWithIfGuard(holder, ref, methodBody, idRef);
		} else {
			methodBody.assign(ref, invoke(holder.getResourcesRef(), androidRes.getResourceMethodName()).arg(idRef));
		}
	}

	private boolean hasContextCompatInClasspath() {
		return processingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT_COMPAT) != null;
	}

	private boolean shouldUseContextGetDrawableMethod() {
		return androidManifest.getMinSdkVersion() >= MIN_SDK_WITH_CONTEXT_GET_DRAWABLE;
	}

	private boolean hasGetDrawableInContext() {
		TypeElement context = processingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.CONTEXT);

		return hasGetDrawable(context);
	}

	private void createCallWithIfGuard(EComponentHolder holder, JFieldRef ref, JBlock methodBody, JFieldRef idRef) {
		JVar resourcesRef = holder.getResourcesRef();
		JConditional guardIf = methodBody._if(holder.classes().BUILD_VERSION.staticRef("SDK_INT").gte(holder.classes().BUILD_VERSION_CODES.staticRef("LOLLIPOP")));
		JBlock ifBlock = guardIf._then();
		ifBlock.assign(ref, holder.getContextRef().invoke("getDrawable").arg(idRef));

		JBlock elseBlock = guardIf._else();
		elseBlock.assign(ref, resourcesRef.invoke("getDrawable").arg(idRef));
	}

	private boolean hasGetDrawable(TypeElement type) {
		if (type == null) {
			return false;
		}

		List<? extends Element> allMembers = processingEnvironment().getElementUtils().getAllMembers(type);
		for (ExecutableElement element : ElementFilter.methodsIn(allMembers)) {
			if (element.getSimpleName().contentEquals("getDrawable")) {
				return true;
			}
		}
		return false;
	}
}
