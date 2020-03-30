/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
import static org.androidannotations.helper.LogHelper.logTagForClassHolder;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.androidannotations.ormlite.helper.OrmLiteClasses;
import org.androidannotations.ormlite.helper.OrmLiteHelper;
import org.androidannotations.ormlite.helper.OrmLiteValidatorHelper;
import org.androidannotations.ormlite.holder.OrmLiteHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> implements MethodInjectionHandler<EComponentHolder> {

	private final InjectHelper<EComponentHolder> injectHelper;

	private final OrmLiteHelper ormLiteHelper;
	private final OrmLiteValidatorHelper ormLiteValidatorHelper;

	public OrmLiteDaoHandler(AndroidAnnotationsEnvironment environment) {
		super(OrmLiteDao.class, environment);
		ormLiteHelper = new OrmLiteHelper(annotationHelper);
		ormLiteValidatorHelper = new OrmLiteValidatorHelper(environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(OrmLiteDao.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		validatorHelper.isNotPrivate(element, validation);

		ormLiteValidatorHelper.hasOrmLiteJars(validation);

		Element param = injectHelper.getParam(element);
		ormLiteValidatorHelper.extendsOrmLiteDao(param, validation, ormLiteHelper);

		ormLiteValidatorHelper.hasASqliteOpenHelperParametrizedType(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentHolder holder, Element element, Element param) {
		OrmLiteHolder ormLiteHolder = holder.getPluginHolder(new OrmLiteHolder(holder));

		AbstractJClass modelClass = getJClass(ormLiteHelper.getEntityType(param).toString());
		AbstractJClass idClass = getJClass(ormLiteHelper.getEntityIdType(param).toString());
		IJExpression modelClassDotClass = modelClass.dotclass();

		AbstractJClass daoClass = getJClass(OrmLiteClasses.DAO).narrow(modelClass, idClass);
		AbstractJClass daoImplClass = codeModelHelper.typeMirrorToJClass(param.asType());

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = ormLiteHolder.getDatabaseHelperRef(databaseHelperTypeMirror);

		IJExpression injectExpr = databaseHelperRef.invoke("getDao").arg(modelClassDotClass);
		if (elementExtendsRuntimeExceptionDao(param)) {
			injectExpr = _new(daoImplClass).arg(cast(daoClass, injectExpr));
		}

		JTryBlock tryBlock = targetBlock._try();
		tryBlock.body().add(fieldRef.assign(injectExpr));

		JCatchBlock catchBlock = tryBlock._catch(getClasses().SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		String fieldName = param.getSimpleName().toString();
		catchBlock.body() //
				.staticInvoke(getClasses().LOG, "e") //
				.arg(logTagForClassHolder(holder))//
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, valid);
	}

	private boolean elementExtendsRuntimeExceptionDao(Element element) {
		TypeMirror elementType = element.asType();
		return annotationHelper.isSubtype(elementType, ormLiteHelper.getRuntimeExceptionDaoParametrizedType());
	}
}
