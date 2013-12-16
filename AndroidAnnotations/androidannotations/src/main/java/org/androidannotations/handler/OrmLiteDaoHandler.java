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
package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.ref;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private TargetAnnotationHelper annotationHelper;

	public OrmLiteDaoHandler(ProcessingEnvironment processingEnvironment) {
		super(OrmLiteDao.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasOrmLiteJars(element, valid);

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.extendsOrmLiteDaoWithValidModelParameter(element, valid);

		validatorHelper.hasASqlLiteOpenHelperParameterizedType(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror modelObjectTypeMirror = annotationHelper.extractAnnotationParameter(element, "model");
		JExpression modelClass = refClass(modelObjectTypeMirror.toString()).dotclass();

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = holder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBody();

		JExpression injectExpr;
		if (elementExtendsRuntimeExceptionDao(element, modelObjectTypeMirror)) {

			injectExpr = classes().RUNTIME_EXCEPTION_DAO//
					.staticInvoke("createDao") //
					.arg(databaseHelperRef.invoke("getConnectionSource")) //
					.arg(modelClass);

		} else {

			injectExpr = databaseHelperRef.invoke("getDao").arg(modelClass);

		}

		JTryBlock tryBlock = initBody._try();
		tryBlock.body().assign(ref(fieldName), injectExpr);

		JCatchBlock catchBlock = tryBlock._catch(classes().SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(classes().LOG, "e") //
				.arg(holder.getGeneratedClass().name()) //
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}

	private boolean elementExtendsRuntimeExceptionDao(Element element, TypeMirror modelObjectTypeMirror) {
		TypeMirror elementType = element.asType();
		TypeElement runtimeExceptionDaoTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.RUNTIME_EXCEPTION_DAO);
		TypeMirror wildcardType = annotationHelper.getTypeUtils().getWildcardType(null, null);
		DeclaredType runtimeExceptionDaoParameterizedType = annotationHelper.getTypeUtils().getDeclaredType(runtimeExceptionDaoTypeElement, modelObjectTypeMirror, wildcardType);
		return annotationHelper.isSubtype(elementType, runtimeExceptionDaoParameterizedType);
	}
}
