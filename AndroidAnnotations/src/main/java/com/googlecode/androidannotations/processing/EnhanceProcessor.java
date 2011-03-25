/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.Enhance;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class EnhanceProcessor extends AnnotationHelper implements ElementProcessor {

	private final IRClass rClass;

	public EnhanceProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Enhance.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		TypeElement typeElement = (TypeElement) element;

		Enhance layoutAnnotation = element.getAnnotation(Enhance.class);
		int layoutIdValue = layoutAnnotation.value();

		String layoutFieldQualifiedName;
		if (layoutIdValue != Id.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			layoutFieldQualifiedName = rInnerClass.getIdQualifiedName(layoutIdValue);
		} else {
			layoutFieldQualifiedName = null;
		}

		String superClassQualifiedName = typeElement.getQualifiedName().toString();

		int packageSeparatorIndex = superClassQualifiedName.lastIndexOf('.');

		String packageName = superClassQualifiedName.substring(0, packageSeparatorIndex);

		String superClassSimpleName = superClassQualifiedName.substring(packageSeparatorIndex + 1);

		MetaActivity activity = new MetaActivity(packageName, superClassSimpleName, layoutFieldQualifiedName);

		metaModel.getMetaActivities().put(element, activity);
	}

	public static final String NEW_CLASS_SUFFIX = "__";

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		ActivityHolder holder = activitiesHolder.create(element);
		
		TypeElement typeElement = (TypeElement) element;

		// Activity
		String annotatedActivityQualifiedName = typeElement.getQualifiedName().toString();

		String subActivityQualifiedName = annotatedActivityQualifiedName + NEW_CLASS_SUFFIX;
		holder.activity = codeModel._class(subActivityQualifiedName);
		
		JClass annotatedActivity = codeModel.directClass(annotatedActivityQualifiedName);

		holder.activity._extends(annotatedActivity);

		JClass bundleClass = codeModel.ref("android.os.Bundle");
		
		// beforeSetContentView
		holder.beforeSetContentView = holder.activity.method(JMod.PRIVATE, codeModel.VOID, "beforeSetContentView_");
		holder.beforeSetContentViewSavedInstanceStateParam = holder.beforeSetContentView.param(bundleClass, "savedInstanceState");
		
		// afterSetContentView
		JMethod afterSetContentView = holder.activity.method(JMod.PRIVATE, codeModel.VOID, "afterSetContentView_");
		afterSetContentView.param(bundleClass, "savedInstanceState");
		
		// onCreate
		JMethod onCreate =	holder.activity.method(JMod.PUBLIC, codeModel.VOID, "onCreate");
		onCreate.annotate(Override.class);

		JVar onCreateSavedInstanceState = onCreate.param(bundleClass, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();
		
		onCreateBody.invoke(holder.beforeSetContentView).arg(onCreateSavedInstanceState);
		
		Enhance layoutAnnotation = element.getAnnotation(Enhance.class);
		int layoutIdValue = layoutAnnotation.value();
		
		String layoutFieldQualifiedName;
		if (layoutIdValue != Id.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			layoutFieldQualifiedName = rInnerClass.getIdQualifiedName(layoutIdValue);
		} else {
			layoutFieldQualifiedName = null;
		}
		
		if (layoutFieldQualifiedName != null) {
			int fieldSuffix = layoutFieldQualifiedName.lastIndexOf('.');
			
			String fieldName = layoutFieldQualifiedName.substring(fieldSuffix+1);
			String rInnerClassName = layoutFieldQualifiedName.substring(0, fieldSuffix);
			
			JFieldRef contentViewId = codeModel.ref(rInnerClassName).staticRef(fieldName);
			
			onCreateBody.invoke("setContentView").arg(contentViewId);
		}
		
		onCreateBody.invoke(afterSetContentView).arg(onCreateSavedInstanceState);

		onCreateBody.invoke(JExpr._super(), onCreate).arg(onCreateSavedInstanceState);
		
	}





}
