/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class FragmentByIdProcessor implements DecoratingElementProcessor {

	private final IdAnnotationHelper annotationHelper;

	public FragmentByIdProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FragmentById.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		TypeMirror nativeFragmentType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT).asType();

		JMethod findFragmentById;
		if (annotationHelper.isSubtype(elementType, nativeFragmentType)) {
			// Injecting native fragment

			findFragmentById = null;

			if (holder.findNativeFragmentById == null) {
				holder.findNativeFragmentById = holder.eBean.method(PRIVATE, classes.FRAGMENT, "findNativeFragmentById");
				JVar idParam = holder.findNativeFragmentById.param(codeModel.INT, "id");

				holder.findNativeFragmentById.javadoc().add("You should check that context is an activity before calling this method");

				JBlock body = holder.findNativeFragmentById.body();

				JVar activityVar = body.decl(classes.ACTIVITY, "activity_", cast(classes.ACTIVITY, holder.contextRef));

				body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
			}

			findFragmentById = holder.findNativeFragmentById;

		} else {
			// Injecting support fragment

			if (holder.findSupportFragmentById == null) {
				holder.findSupportFragmentById = holder.eBean.method(PRIVATE, classes.SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
				JVar idParam = holder.findSupportFragmentById.param(codeModel.INT, "id");

				JBlock body = holder.findSupportFragmentById.body();

				body._if(holder.contextRef._instanceof(classes.FRAGMENT_ACTIVITY).not())._then()._return(_null());

				JVar activityVar = body.decl(classes.FRAGMENT_ACTIVITY, "activity_", cast(classes.FRAGMENT_ACTIVITY, holder.contextRef));

				body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
			}

			findFragmentById = holder.findSupportFragmentById;
		}

		JBlock methodBody = holder.afterSetContentView.body();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, Res.ID, true);

		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), invoke(findFragmentById).arg(idRef)));
	}
}
