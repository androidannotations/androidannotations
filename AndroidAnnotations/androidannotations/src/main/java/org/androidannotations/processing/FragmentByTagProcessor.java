/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing;

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

import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class FragmentByTagProcessor implements DecoratingElementProcessor {

	private final AnnotationHelper annotationHelper;

	public FragmentByTagProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FragmentByTag.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		FragmentByTag annotation = element.getAnnotation(FragmentByTag.class);
		String tagValue = annotation.value();

		if (tagValue.equals("")) {
			tagValue = fieldName;
		}

		TypeMirror nativeFragmentType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT).asType();

		JMethod findFragmentByTag;
		if (annotationHelper.isSubtype(elementType, nativeFragmentType)) {
			// Injecting native fragment

			findFragmentByTag = null;

			if (holder.findNativeFragmentByTag == null) {
				holder.findNativeFragmentByTag = holder.generatedClass.method(PRIVATE, classes.FRAGMENT, "findNativeFragmentByTag");
				JVar tagParam = holder.findNativeFragmentByTag.param(classes.STRING, "tag");

				JBlock body = holder.findNativeFragmentByTag.body();
				body._if(holder.contextRef._instanceof(classes.ACTIVITY).not())._then()._return(_null());

				JVar activityVar = body.decl(classes.ACTIVITY, "activity_", cast(classes.ACTIVITY, holder.contextRef));

				body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
			}

			findFragmentByTag = holder.findNativeFragmentByTag;

		} else {
			// Injecting support fragment

			if (holder.findSupportFragmentByTag == null) {
				holder.findSupportFragmentByTag = holder.generatedClass.method(PRIVATE, classes.SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
				JVar tagParam = holder.findSupportFragmentByTag.param(classes.STRING, "tag");

				JBlock body = holder.findSupportFragmentByTag.body();

				body._if(holder.contextRef._instanceof(classes.FRAGMENT_ACTIVITY).not())._then()._return(_null());

				JVar activityVar = body.decl(classes.FRAGMENT_ACTIVITY, "activity_", cast(classes.FRAGMENT_ACTIVITY, holder.contextRef));

				body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));
			}

			findFragmentByTag = holder.findSupportFragmentByTag;
		}

		JBlock methodBody = holder.onViewChanged().body();
		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), invoke(findFragmentByTag).arg(lit(tagValue))));
	}
}
