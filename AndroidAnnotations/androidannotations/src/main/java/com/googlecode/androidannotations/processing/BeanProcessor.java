/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.googlecode.androidannotations.processing.EBeanProcessor.GET_INSTANCE_METHOD_NAME;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.api.SetContentViewAware;
import com.googlecode.androidannotations.helper.ElementHelper;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;

public class BeanProcessor implements ElementProcessor {

	private TargetAnnotationHelper annotationHelper;

	public BeanProcessor(ProcessingEnvironment processingEnv, AnnotationElements validatedModel) {
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Bean.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {

		if (annotationHelper.isAssignable(element.asType(), Collection.class)) {
			processCollection(element, codeModel, eBeansHolder);
			processCollectionItems(element, codeModel, eBeansHolder);
			return;
		}

		processSingle(element, codeModel, eBeansHolder);

	}

	private void processCollection(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);
		DeclaredType providedImpl = annotationHelper.extractAnnotationClassValue(element);
		String fieldName = element.getSimpleName().toString();

		if (providedImpl != null) {
			if (providedImpl.toString().startsWith("java.")) {
				assignCollection(codeModel, holder.init.body(), codeModel.ref(providedImpl.toString()), fieldName);
				return;
			}
			processSingle(element, codeModel, eBeansHolder);
		} else {
			TypeMirror elementDeclaredType = element.asType();
			TypeMirror typed = ElementHelper.getAsDeclaredType(element).getTypeArguments().get(0);
			if (annotationHelper.isAssignable(Collection.class, elementDeclaredType) || annotationHelper.isAssignable(elementDeclaredType, List.class)) {
				assignNarrowedCollection(codeModel, holder.init.body(), ArrayList.class.getCanonicalName(), fieldName, typed);
				return;
			}
			if (annotationHelper.isAssignable(elementDeclaredType, Set.class)) {
				assignNarrowedCollection(codeModel, holder.init.body(), HashSet.class.getCanonicalName(), fieldName, typed);
				return;
			}
		}
	}

	private void assignNarrowedCollection(JCodeModel codeModel, JBlock body, String providedImpl, String fieldName, TypeMirror narrowed) {
		JClass injectedClass = codeModel.ref(providedImpl).narrow(codeModel.ref(narrowed.toString()));
		assignCollection(codeModel, body, injectedClass, fieldName);
	}

	private void assignCollection(JCodeModel codeModel, JBlock body, JClass injectedClass, String fieldName) {
		JInvocation getInstance = JExpr._new(injectedClass);
		JFieldRef ref = ref(fieldName);
		body._if(ref.eq(JExpr._null()))._then().assign(ref, getInstance);
	}

	@SuppressWarnings("unchecked")
	private void processCollectionItems(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		JBlock init = holder.init.body();
		JBlock afterSetContentView = holder.afterSetContentView.body();
		String fieldName = element.getSimpleName().toString();

		if (holder.afterSetContentView != null) {
			afterSetContentView = holder.afterSetContentView.body();
			JForEach forEach = afterSetContentView.forEach(codeModel.ref(Object.class), "item", ref(fieldName));
			forEach.body().invoke(cast(codeModel.ref(SetContentViewAware.class), ref("item")), SetContentViewAware.SIGNATURE);
		}

		List<? extends AnnotationValue> eBeans = (List<? extends AnnotationValue>) annotationHelper.extractAnnotationClassAttrVal(element, "items");
		if (eBeans != null) {
			for (AnnotationValue bean : eBeans) {
				JClass injectedClass = codeModel.ref(bean.getValue().toString() + GENERATION_SUFFIX);
				JInvocation getInstance = injectedClass.staticInvoke(GET_INSTANCE_METHOD_NAME).arg(holder.contextRef);
				init.invoke(ref(fieldName), "add").arg(getInstance);
			}
		}

	}

	private void processSingle(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);
		DeclaredType targetAnnotationClassValue = annotationHelper.extractAnnotationClassValue(element);

		TypeMirror elementType;
		if (targetAnnotationClassValue != null) {
			elementType = targetAnnotationClassValue;
		} else {
			elementType = element.asType();
		}

		String fieldName = element.getSimpleName().toString();

		String typeQualifiedName = elementType.toString();

		JClass injectedClass = codeModel.ref(typeQualifiedName + GENERATION_SUFFIX);

		{
			// getInstance
			JBlock body = holder.init.body();

			JInvocation getInstance = injectedClass.staticInvoke(GET_INSTANCE_METHOD_NAME).arg(holder.contextRef);
			body.assign(ref(fieldName), getInstance);
		}

		{
			// afterSetContentView
			if (holder.afterSetContentView != null) {
				JBlock body = holder.afterSetContentView.body();

				body.invoke(cast(injectedClass, ref(fieldName)), holder.afterSetContentView);
			}
		}
	}
}
