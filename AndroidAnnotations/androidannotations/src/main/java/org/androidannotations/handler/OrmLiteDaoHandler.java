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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.OrmLiteHelper;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final OrmLiteHelper ormLiteHelper;
	private final TargetAnnotationHelper helper;

	public OrmLiteDaoHandler(ProcessingEnvironment processingEnvironment) {
		super(OrmLiteDao.class, processingEnvironment);
		helper = new TargetAnnotationHelper(processingEnv, getTarget());
		ormLiteHelper = new OrmLiteHelper(helper);
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasOrmLiteJars(element, valid);

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.extendsOrmLiteDao(element, valid, ormLiteHelper);

		validatorHelper.hasASqlLiteOpenHelperParameterizedType(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		JClass modelClass = refClass(ormLiteHelper.getEntityType(element));
		JClass idClass = refClass(ormLiteHelper.getEntityIdType(element));
		JExpression modelClassDotClass = modelClass.dotclass();

		JClass daoClass = refClass(CanonicalNameConstants.DAO).narrow(modelClass, idClass);

		TypeMirror databaseHelperTypeMirror = helper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = holder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBody();

		JExpression injectExpr = databaseHelperRef.invoke("getDao").arg(modelClassDotClass);
		if (elementExtendsRuntimeExceptionDao(element)) {
			JClass daoImplClass = codeModelHelper.typeMirrorToJClass(element.asType(), holder);
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
		return helper.isSubtype(elementType, ormLiteHelper.getRuntimeExceptionDaoParametrizedType());
	}
}
