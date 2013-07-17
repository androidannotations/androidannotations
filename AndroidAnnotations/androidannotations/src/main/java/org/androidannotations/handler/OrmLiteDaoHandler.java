/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.sun.codemodel.JExpr.ref;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private TargetAnnotationHelper helper;
	private DeclaredType daoParametrizedType;
	private DeclaredType runtimeExceptionDaoParametrizedType;

	public OrmLiteDaoHandler(ProcessingEnvironment processingEnvironment) {
		super(OrmLiteDao.class, processingEnvironment);
		helper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasOrmLiteJars(element, valid);

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.extendsOrmLiteDao(element, valid);

		validatorHelper.hasASqlLiteOpenHelperParameterizedType(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		createDaoParametrizedTypes();

		String fieldName = element.getSimpleName().toString();

		JClass modelClass = getEntityAndIdClass(element, holder);
		JExpression modelClassDotClass = modelClass.dotclass();

		TypeMirror databaseHelperTypeMirror = helper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = holder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBody();

		JExpression injectExpr;
		if (elementExtendsRuntimeExceptionDao(element)) {

			injectExpr = classes().RUNTIME_EXCEPTION_DAO//
					.staticInvoke("createDao") //
					.arg(databaseHelperRef.invoke("getConnectionSource")) //
					.arg(modelClassDotClass);

		} else {

			injectExpr = databaseHelperRef.invoke("getDao").arg(modelClassDotClass);

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

	private boolean elementExtendsRuntimeExceptionDao(Element element) {
		TypeMirror elementType = element.asType();
		return helper.isSubtype(elementType, runtimeExceptionDaoParametrizedType);
	}

	private JClass getEntityAndIdClass(Element element, EComponentHolder holder) {
		if (isSubtypeOfDao(element.asType())) {
			DeclaredType declaredType = (DeclaredType) element.asType();
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
			if (typeArguments.size() == 2) {
				return holder.refClass(typeArguments.get(0).toString());
			}
		}

		List<? extends TypeMirror> superTypes = helper.directSupertypes(element.asType());
		for (TypeMirror superType : superTypes) {

			if (superType.getKind() == TypeKind.DECLARED && isSubtypeOfDao(superType)) {

				DeclaredType superDeclaredType = (DeclaredType) superType;
				List<? extends TypeMirror> typeArguments = superDeclaredType.getTypeArguments();
				if (typeArguments.size() == 2) {
					return holder.refClass(typeArguments.get(0).toString());
				}
			}
		}
		return null;
	}

	private boolean isSubtypeOfDao(TypeMirror type) {
		return helper.isSubtype(type, daoParametrizedType) || helper.isSubtype(type, runtimeExceptionDaoParametrizedType);
	}

	/*
	 * This method is not in the constructor because it prevents tests from
	 * succeeding
	 */
	private void createDaoParametrizedTypes() {
		if (daoParametrizedType == null) {
			TypeMirror wildcardType = helper.getTypeUtils().getWildcardType(null, null);
			TypeElement daoTypeElement = helper.typeElementFromQualifiedName(CanonicalNameConstants.DAO);
			daoParametrizedType = helper.getTypeUtils().getDeclaredType(daoTypeElement, wildcardType, wildcardType);
			TypeElement runtimeExceptionDaoTypeElement = helper.typeElementFromQualifiedName(CanonicalNameConstants.RUNTIME_EXCEPTION_DAO);
			runtimeExceptionDaoParametrizedType = helper.getTypeUtils().getDeclaredType(runtimeExceptionDaoTypeElement, wildcardType, wildcardType);
		}
	}
}
