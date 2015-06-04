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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.WakeLock;
import org.androidannotations.annotations.WakeLock.Flag;
import org.androidannotations.annotations.WakeLock.Level;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class WakeLockHandler extends BaseAnnotationHandler<EComponentHolder> {

	public WakeLockHandler(ProcessingEnvironment processingEnvironment) {
		super(WakeLock.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.doesNotHaveTraceAnnotationAndReturnValue(executableElement, validatedElements, valid);

		validatorHelper.doesNotUseFlagsWithPartialWakeLock(element, validatedElements, valid);

		validatorHelper.hasWakeLockPermission(element, androidManifest, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.isNotFinal(element, valid);

	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		WakeLock annotation = executableElement.getAnnotation(WakeLock.class);

		String tag = extractTag(executableElement);
		Level level = annotation.level();
		Flag[] flags = annotation.flags();

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JBlock previousMethodBody = codeModelHelper.removeBody(method);

		JBlock methodBody = method.body();

		JExpression levelAndFlags = classes().POWER_MANAGER.staticRef(level.name());
		if (flags.length > 0) {
			for (Flag flag : flags) {
				levelAndFlags = levelAndFlags.bor(classes().POWER_MANAGER.staticRef(flag.name()));
			}
		}

		JInvocation newWakeLock = holder.getPowerManagerRef().invoke("newWakeLock").arg(levelAndFlags).arg(JExpr.lit(tag));

		JVar wakeLock = methodBody.decl(classes().WAKE_LOCK, "wakeLock", JExpr._null());

		JTryBlock tryBlock = methodBody._try();
		tryBlock.body().assign(wakeLock, newWakeLock);
		tryBlock.body().add(wakeLock.invoke("acquire"));
		tryBlock.body().add(previousMethodBody);

		JBlock finallyBlock = tryBlock._finally();
		JConditional ifStatement = finallyBlock._if(wakeLock.ne(JExpr._null()));
		ifStatement._then().add(wakeLock.invoke("release"));
	}

	private String extractTag(Element element) {
		WakeLock annotation = element.getAnnotation(WakeLock.class);
		String tag = annotation.tag();
		if (WakeLock.DEFAULT_TAG.equals(tag)) {
			tag = element.getEnclosingElement().getSimpleName().toString() + "." + element.getSimpleName().toString();
		}
		return tag;
	}
}
