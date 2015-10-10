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

import static com.helger.jcodemodel.JExpr.assign;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidSystemServices;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;

public class SystemServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

	public SystemServiceHandler(AndroidAnnotationsEnvironment environment) {
		super(SystemService.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.androidService(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror serviceType = element.asType();
		String fieldTypeQualifiedName = serviceType.toString();

		JFieldRef serviceRef = new AndroidSystemServices(getEnvironment()).getServiceConstantRef(serviceType);

		JBlock methodBody = holder.getInitBodyInjectionBlock();

		if (CanonicalNameConstants.APP_WIDGET_MANAGER.equals(fieldTypeQualifiedName)) {
			createSpecialInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody, 21, "LOLLIPOP", getClasses().APP_WIDGET_MANAGER, "getInstance", true);
		} else {
			methodBody.add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
		}
	}

	@SuppressWarnings("checkstyle:parameternumber")
	private void createSpecialInjection(EComponentHolder holder, String fieldName, String fieldTypeQualifiedName, JFieldRef serviceRef, JBlock methodBody, int apiLevel, String apiLevelName,
										AbstractJClass serviceClass, String injectionMethodName, boolean contextNeeded) {
		if (getEnvironment().getAndroidManifest().getMinSdkVersion() >= apiLevel) {
			methodBody.add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
		} else {
			JInvocation injectionMethodInvokation = serviceClass.staticInvoke(injectionMethodName);
			if (contextNeeded) {
				injectionMethodInvokation.arg(holder.getContextRef());
			}
			IJStatement oldInjection = assign(ref(fieldName), injectionMethodInvokation);

			if (isApiOnClasspath(apiLevelName)) {
				JConditional conditional = methodBody._if(getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef(apiLevelName)));
				conditional._then().add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
				conditional._else().add(oldInjection);
			} else {
				methodBody.add(oldInjection);
			}
		}
	}

	private IJStatement createNormalInjection(EComponentHolder holder, String fieldName, String fieldTypeQualifiedName, JFieldRef serviceRef, JBlock methodBody) {
		return assign(ref(fieldName), cast(getJClass(fieldTypeQualifiedName), holder.getContextRef().invoke("getSystemService").arg(serviceRef)));
	}

	private boolean isApiOnClasspath(String apiName) {
		TypeElement typeElement = getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.BUILD_VERSION_CODES);
		for (Element element : typeElement.getEnclosedElements()) {
			if (element.getSimpleName().contentEquals(apiName)) {
				return true;
			}
		}
		return false;
	}
}
