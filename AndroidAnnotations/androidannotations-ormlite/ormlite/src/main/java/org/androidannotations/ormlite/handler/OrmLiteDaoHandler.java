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
package org.androidannotations.ormlite.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.androidannotations.ormlite.helper.OrmLiteClasses;
import org.androidannotations.ormlite.helper.OrmLiteHelper;
import org.androidannotations.ormlite.helper.OrmLiteValidatorHelper;
import org.androidannotations.ormlite.holder.OrmLiteHolder;
import org.androidannotations.process.ElementValidation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final OrmLiteHelper ormLiteHelper;
	private final OrmLiteValidatorHelper ormLiteValidatorHelper;

	public OrmLiteDaoHandler(AndroidAnnotationsEnvironment environment) {
		super(OrmLiteDao.class, environment);
		ormLiteHelper = new OrmLiteHelper(annotationHelper);
		ormLiteValidatorHelper = new OrmLiteValidatorHelper(environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		ormLiteValidatorHelper.hasOrmLiteJars(validation);

		ormLiteValidatorHelper.extendsOrmLiteDao(element, validation, ormLiteHelper);

		ormLiteValidatorHelper.hasASqliteOpenHelperParametrizedType(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		OrmLiteHolder ormLiteHolder = holder.getPluginHolder(new OrmLiteHolder(holder));

		String fieldName = element.getSimpleName().toString();

		JClass modelClass = refClass(ormLiteHelper.getEntityType(element));
		JClass idClass = refClass(ormLiteHelper.getEntityIdType(element));
		JExpression modelClassDotClass = modelClass.dotclass();

		JClass daoClass = refClass(OrmLiteClasses.DAO).narrow(modelClass, idClass);

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = ormLiteHolder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBody();

		JExpression injectExpr = databaseHelperRef.invoke("getDao").arg(modelClassDotClass);
		if (elementExtendsRuntimeExceptionDao(element)) {
			JClass daoImplClass = codeModelHelper.typeMirrorToJClass(element.asType());
			injectExpr = _new(daoImplClass).arg(cast(daoClass, injectExpr));
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
		return annotationHelper.isSubtype(elementType, ormLiteHelper.getRuntimeExceptionDaoParametrizedType());
	}
}
