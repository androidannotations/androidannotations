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

import static com.sun.codemodel.JExpr.assign;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.SystemService;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JStatement;

public class SystemServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

	public SystemServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(SystemService.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.androidService(androidSystemServices, element, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror serviceType = element.asType();
		String fieldTypeQualifiedName = serviceType.toString();

		JFieldRef serviceRef = androidSystemServices.getServiceConstant(serviceType, holder);

		JBlock methodBody = holder.getInitBody();

		if (CanonicalNameConstants.APP_WIDGET_MANAGER.equals(fieldTypeQualifiedName)) {
			createSpecialInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody, 21, "LOLLIPOP", holder.classes().APP_WIDGET_MANAGER, "getInstance", true);
		} else {
			methodBody.add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
		}
	}

	@SuppressWarnings("checkstyle:parameternumber")
	private void createSpecialInjection(EComponentHolder holder, String fieldName, String fieldTypeQualifiedName, JFieldRef serviceRef, JBlock methodBody, int apiLevel, String apiLevelName,
			JClass serviceClass, String injectionMethodName, boolean contextNeeded) {
		if (androidManifest.getMinSdkVersion() >= apiLevel) {
			methodBody.add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
		} else {
			JInvocation injectionMethodInvokation = serviceClass.staticInvoke(injectionMethodName);
			if (contextNeeded) {
				injectionMethodInvokation.arg(holder.getContextRef());
			}
			JStatement oldInjection = (JStatement) assign(ref(fieldName), injectionMethodInvokation);

			if (isApiOnClasspath(apiLevelName)) {
				JConditional conditional = methodBody._if(holder.classes().BUILD_VERSION.staticRef("SDK_INT").gte(holder.classes().BUILD_VERSION_CODES.staticRef(apiLevelName)));
				conditional._then().add(createNormalInjection(holder, fieldName, fieldTypeQualifiedName, serviceRef, methodBody));
				conditional._else().add(oldInjection);
			} else {
				methodBody.add(oldInjection);
			}
		}
	}

	private JStatement createNormalInjection(EComponentHolder holder, String fieldName, String fieldTypeQualifiedName, JFieldRef serviceRef, JBlock methodBody) {
		return (JStatement) assign(ref(fieldName), cast(refClass(fieldTypeQualifiedName), holder.getContextRef().invoke("getSystemService").arg(serviceRef)));
	}

	private boolean isApiOnClasspath(String apiName) {
		TypeElement typeElement = processingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.BUILD_VERSION_CODES);
		for (Element element : typeElement.getEnclosedElements()) {
			if (element.getSimpleName().contentEquals(apiName)) {
				return true;
			}
		}
		return false;
	}
}
