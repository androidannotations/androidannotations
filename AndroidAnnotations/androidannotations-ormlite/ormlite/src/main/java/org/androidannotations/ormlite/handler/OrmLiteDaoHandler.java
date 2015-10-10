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

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.androidannotations.ormlite.helper.OrmLiteClasses;
import org.androidannotations.ormlite.helper.OrmLiteHelper;
import org.androidannotations.ormlite.helper.OrmLiteValidatorHelper;
import org.androidannotations.ormlite.holder.OrmLiteHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

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

		AbstractJClass modelClass = getJClass(ormLiteHelper.getEntityType(element).toString());
		AbstractJClass idClass = getJClass(ormLiteHelper.getEntityIdType(element).toString());
		IJExpression modelClassDotClass = modelClass.dotclass();

		AbstractJClass daoClass = getJClass(OrmLiteClasses.DAO).narrow(modelClass, idClass);

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = ormLiteHolder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBodyInjectionBlock();

		IJExpression injectExpr = databaseHelperRef.invoke("getDao").arg(modelClassDotClass);
		if (elementExtendsRuntimeExceptionDao(element)) {
			AbstractJClass daoImplClass = codeModelHelper.typeMirrorToJClass(element.asType());
			injectExpr = _new(daoImplClass).arg(cast(daoClass, injectExpr));
		}

		JTryBlock tryBlock = initBody._try();
		tryBlock.body().assign(ref(fieldName), injectExpr);

		JCatchBlock catchBlock = tryBlock._catch(getClasses().SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(getClasses().LOG, "e") //
				.arg(holder.getGeneratedClass().name()) //
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}

	private boolean elementExtendsRuntimeExceptionDao(Element element) {
		TypeMirror elementType = element.asType();
		return annotationHelper.isSubtype(elementType, ormLiteHelper.getRuntimeExceptionDaoParametrizedType());
	}
}
