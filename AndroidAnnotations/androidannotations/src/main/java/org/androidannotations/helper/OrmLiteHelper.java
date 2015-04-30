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
package org.androidannotations.helper;

import static com.sun.codemodel.JExpr._null;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.androidannotations.holder.HasLifecycleMethods;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldVar;

public class OrmLiteHelper {

	private final TargetAnnotationHelper helper;

	private DeclaredType daoParametrizedType;
	private DeclaredType runtimeExceptionDaoParametrizedType;

	private TypeElement daoTypeElement;
	private TypeElement runtimeExceptionDaoTypeElement;

	public OrmLiteHelper(TargetAnnotationHelper helper) {
		this.helper = helper;
	}

	public TypeMirror getEntityType(Element element) {
		return getEntityOrIdType(element, 0);
	}

	public TypeMirror getEntityIdType(Element element) {
		return getEntityOrIdType(element, 1);
	}

	private TypeMirror getEntityOrIdType(Element element, int index) {
		if (isSubtypeOfDao(element.asType())) {
			DeclaredType declaredType = (DeclaredType) element.asType();
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
			if (typeArguments.size() == 2) {
				return typeArguments.get(index);
			}
		}

		List<? extends TypeMirror> superTypes = helper.directSupertypes(element.asType());
		for (TypeMirror superType : superTypes) {

			if (superType.getKind() == TypeKind.DECLARED && isSubtypeOfDao(superType)) {

				DeclaredType superDeclaredType = (DeclaredType) superType;
				List<? extends TypeMirror> typeArguments = superDeclaredType.getTypeArguments();
				if (typeArguments.size() == 2) {
					return typeArguments.get(index);
				}
			}
		}
		return null;
	}

	private boolean isSubtypeOfDao(TypeMirror type) {
		return helper.isSubtype(type, daoParametrizedType) || helper.isSubtype(type, runtimeExceptionDaoParametrizedType);
	}

	public DeclaredType getDaoParametrizedType() {
		if (daoParametrizedType == null) {
			createDaoParametrizedTypes();
		}
		return daoParametrizedType;
	}

	public DeclaredType getRuntimeExceptionDaoParametrizedType() {
		if (runtimeExceptionDaoParametrizedType == null) {
			createDaoParametrizedTypes();
		}
		return runtimeExceptionDaoParametrizedType;
	}

	public DeclaredType getTypedDao(Element element) {
		if (daoTypeElement == null) {
			createDaoParametrizedTypes();
		}
		return helper.getTypeUtils().getDeclaredType(daoTypeElement, getEntityType(element), getEntityIdType(element));
	}

	public DeclaredType getTypedRuntimeExceptionDao(Element element) {
		if (runtimeExceptionDaoTypeElement == null) {
			createDaoParametrizedTypes();
		}
		return helper.getTypeUtils().getDeclaredType(runtimeExceptionDaoTypeElement, getEntityType(element), getEntityIdType(element));
	}

	private void createDaoParametrizedTypes() {
		daoTypeElement = helper.typeElementFromQualifiedName(CanonicalNameConstants.DAO);
		runtimeExceptionDaoTypeElement = helper.typeElementFromQualifiedName(CanonicalNameConstants.RUNTIME_EXCEPTION_DAO);

		Types typeUtils = helper.getTypeUtils();
		TypeMirror wildcardType = typeUtils.getWildcardType(null, null);
		daoParametrizedType = helper.getTypeUtils().getDeclaredType(daoTypeElement, wildcardType, wildcardType);
		runtimeExceptionDaoParametrizedType = helper.getTypeUtils().getDeclaredType(runtimeExceptionDaoTypeElement, wildcardType, wildcardType);
	}

	public static void injectReleaseInDestroy(JFieldVar databaseHelperRef, HasLifecycleMethods holder, ProcessHolder.Classes classes) {
		JBlock destroyBody = holder.getOnDestroyBeforeSuperBlock();

		destroyBody.staticInvoke(classes.OPEN_HELPER_MANAGER, "releaseHelper");
		destroyBody.assign(databaseHelperRef, _null());
	}
}
