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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.FragmentByTag;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class FragmentByTagProcessor implements ElementProcessor {

	private final AnnotationHelper annotationHelper;

	public FragmentByTagProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FragmentByTag.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {

		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		FragmentByTag annotation = element.getAnnotation(FragmentByTag.class);
		String tagValue = annotation.value();

		if (tagValue.equals("")) {
			tagValue = fieldName;
		}

		JClass activityClass = holder.refClass("android.app.Activity");
		JClass stringClass = holder.refClass("java.lang.String");
		JClass supportFragmentActivityClass = holder.refClass("android.support.v4.app.FragmentActivity");

		TypeMirror nativeFragmentType = annotationHelper.typeElementFromQualifiedName("android.app.Fragment").asType();

		JMethod findFragmentByTag;
		if (annotationHelper.isSubtype(elementType, nativeFragmentType)) {
			// Injecting native fragment

			findFragmentByTag = null;

			if (holder.findNativeFragmentByTag == null) {
				JClass fragmentClass = holder.refClass("android.app.Fragment");
				holder.findNativeFragmentByTag = holder.eBean.method(PRIVATE, fragmentClass, "findNativeFragmentByTag");
				JVar tagParam = holder.findNativeFragmentByTag.param(stringClass, "tag");

				holder.findNativeFragmentByTag.javadoc().add("You should check that context is an activity before calling this method");

				JBlock body = holder.findNativeFragmentByTag.body();

				JVar activityVar = body.decl(activityClass, "activity_", cast(activityClass, holder.contextRef));

				body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
			}

			findFragmentByTag = holder.findNativeFragmentByTag;

		} else {
			// Injecting support fragment

			if (holder.findSupportFragmentByTag == null) {
				JClass fragmentClass = holder.refClass("android.support.v4.app.Fragment");
				holder.findSupportFragmentByTag = holder.eBean.method(PRIVATE, fragmentClass, "findSupportFragmentByTag");
				JVar tagParam = holder.findSupportFragmentByTag.param(stringClass, "tag");

				JBlock body = holder.findSupportFragmentByTag.body();

				body._if(holder.contextRef._instanceof(supportFragmentActivityClass).not())._then()._return(_null());

				JVar activityVar = body.decl(supportFragmentActivityClass, "activity_", cast(supportFragmentActivityClass, holder.contextRef));

				body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));
			}

			findFragmentByTag = holder.findSupportFragmentByTag;
		}

		JBlock methodBody = holder.afterSetContentView.body();
		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), invoke(findFragmentByTag).arg(lit(tagValue))));
	}
}
