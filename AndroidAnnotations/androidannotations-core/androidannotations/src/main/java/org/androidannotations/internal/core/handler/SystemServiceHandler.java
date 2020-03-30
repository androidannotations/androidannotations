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

import static com.helger.jcodemodel.JExpr.cast;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidSystemServices;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JOp;

public class SystemServiceHandler extends BaseAnnotationHandler<EComponentHolder> implements MethodInjectionHandler<EComponentHolder> {

	private final InjectHelper<EComponentHolder> injectHelper;

	public SystemServiceHandler(AndroidAnnotationsEnvironment environment) {
		super(SystemService.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(SystemService.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		validatorHelper.androidService(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentHolder holder, Element element, Element param) {
		TypeMirror serviceType = param.asType();
		String fieldTypeQualifiedName = serviceType.toString();

		JFieldRef serviceRef = new AndroidSystemServices(getEnvironment()).getServiceConstantRef(serviceType);
		if (CanonicalNameConstants.APP_WIDGET_MANAGER.equals(fieldTypeQualifiedName)) {
			targetBlock.add(fieldRef.assign(createSpecialInjection(holder, fieldTypeQualifiedName, serviceRef, 21, "LOLLIPOP", getClasses().APP_WIDGET_MANAGER, "getInstance", true)));
		} else {
			targetBlock.add(fieldRef.assign(createNormalInjection(holder, fieldTypeQualifiedName, serviceRef)));
		}
	}

	@SuppressWarnings("checkstyle:parameternumber")
	private IJExpression createSpecialInjection(EComponentHolder holder, String fieldTypeQualifiedName, JFieldRef serviceRef, int apiLevel, String apiLevelName, AbstractJClass serviceClass,
			String injectionMethodName, boolean invocationRequiresContext) {
		if (getEnvironment().getAndroidManifest().getMinSdkVersion() >= apiLevel) {
			return createNormalInjection(holder, fieldTypeQualifiedName, serviceRef);
		} else {
			JInvocation serviceClassInvocation = serviceClass.staticInvoke(injectionMethodName);
			if (invocationRequiresContext) {
				serviceClassInvocation.arg(holder.getContextRef());
			}
			if (isApiOnClasspath(apiLevelName)) {
				IJExpression condition = getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef(apiLevelName));
				IJExpression normalInjection = createNormalInjection(holder, fieldTypeQualifiedName, serviceRef);
				return JOp.cond(condition, normalInjection, serviceClassInvocation);
			} else {
				return serviceClassInvocation;
			}
		}
	}

	private IJExpression createNormalInjection(EComponentHolder holder, String fieldTypeQualifiedName, JFieldRef serviceRef) {
		return cast(getJClass(fieldTypeQualifiedName), getAppropriateContextRef(holder, fieldTypeQualifiedName).invoke("getSystemService").arg(serviceRef));
	}

	private IJExpression getAppropriateContextRef(EComponentHolder holder, String fieldTypeQualifiedName) {
		if (CanonicalNameConstants.WIFI_MANAGER.equals(fieldTypeQualifiedName) || CanonicalNameConstants.AUDIO_MANAGER.equals(fieldTypeQualifiedName)) {
			return holder.getContextRef().invoke("getApplicationContext");
		}

		return holder.getContextRef();
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

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, valid);
	}
}
