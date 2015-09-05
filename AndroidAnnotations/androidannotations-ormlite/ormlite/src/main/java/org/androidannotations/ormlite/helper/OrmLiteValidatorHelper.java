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
package org.androidannotations.ormlite.helper;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.ormlite.annotations.OrmLiteDao;

public class OrmLiteValidatorHelper {

	public AnnotationHelper annotationHelper;

	public OrmLiteValidatorHelper(AndroidAnnotationsEnvironment environment) {
		annotationHelper = new AnnotationHelper(environment);
	}

	public void hasOrmLiteJars(ElementValidation valid) {
		Elements elementUtils = annotationHelper.getElementUtils();
		if (elementUtils.getTypeElement(OrmLiteClasses.DAO) == null) {
			valid.addError("Could not find the OrmLite framework in the classpath, the following class is missing: " + OrmLiteClasses.DAO);
		}
	}

	public void extendsOrmLiteDao(Element element, ElementValidation valid, OrmLiteHelper ormLiteHelper) {
		if (!valid.isValid()) {
			// we now that the element is invalid. early exit as the reason
			// could be missing ormlite classes
			return;
		}

		TypeMirror elementTypeMirror = element.asType();
		Types typeUtils = annotationHelper.getTypeUtils();

		DeclaredType daoParametrizedType = ormLiteHelper.getDaoParametrizedType();
		DeclaredType runtimeExceptionDaoParametrizedType = ormLiteHelper.getRuntimeExceptionDaoParametrizedType();

		// Checks that elementType extends Dao<?, ?> or
		// RuntimeExceptionDao<?, ?>
		if (!annotationHelper.isSubtype(elementTypeMirror, daoParametrizedType) && !annotationHelper.isSubtype(elementTypeMirror, runtimeExceptionDaoParametrizedType)) {
			valid.addError("%s can only be used on an element that extends " + daoParametrizedType.toString() //
					+ " or " + runtimeExceptionDaoParametrizedType.toString());
			return;
		}

		if (annotationHelper.isSubtype(elementTypeMirror, runtimeExceptionDaoParametrizedType) //
				&& !typeUtils.isAssignable(ormLiteHelper.getTypedRuntimeExceptionDao(element), elementTypeMirror)) {

			boolean hasConstructor = false;
			Element elementType = typeUtils.asElement(elementTypeMirror);
			DeclaredType daoWithTypedParameters = ormLiteHelper.getTypedDao(element);
			for (ExecutableElement constructor : ElementFilter.constructorsIn(elementType.getEnclosedElements())) {
				List<? extends VariableElement> parameters = constructor.getParameters();
				if (parameters.size() == 1) {
					TypeMirror type = parameters.get(0).asType();
					if (annotationHelper.isSubtype(type, daoWithTypedParameters)) {
						hasConstructor = true;
					}
				}
			}
			if (!hasConstructor) {
				valid.addError(elementTypeMirror.toString() + " requires a constructor that takes only a " + daoWithTypedParameters.toString());
			}
		}
	}

	public void hasASqliteOpenHelperParametrizedType(Element element, ElementValidation valid) {
		TypeMirror helperType = annotationHelper.extractAnnotationParameter(element, OrmLiteDao.class.getName(), "helper");

		TypeMirror openHelperType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.SQLITE_OPEN_HELPER).asType();
		if (!annotationHelper.isSubtype(helperType, openHelperType)) {
			valid.addError("%s helper() parameter must extend " + CanonicalNameConstants.SQLITE_OPEN_HELPER);
		}
	}
}
