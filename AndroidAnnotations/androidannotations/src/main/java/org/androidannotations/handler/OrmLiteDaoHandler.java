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

import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private TargetAnnotationHelper annotationHelper;

	public OrmLiteDaoHandler(ProcessingEnvironment processingEnvironment) {
		super(OrmLiteDao.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.hasOrmLiteJars(element, valid);

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.extendsOrmLiteDaoWithValidModelParameter(element, valid);

		validatorHelper.hasASqlLiteOpenHelperParameterizedType(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror modelObjectTypeMirror = annotationHelper.extractAnnotationParameter(element, "model");
		JExpression modelClass = holder.refClass(modelObjectTypeMirror.toString()).dotclass();

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = holder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInitBody();

		JTryBlock tryBlock = initBody._try();
		tryBlock.body().assign(ref(fieldName), databaseHelperRef.invoke("getDao").arg(modelClass));

		JCatchBlock catchBlock = tryBlock._catch(holder.classes().SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(holder.classes().LOG, "e") //
				.arg(holder.getGeneratedClass().name()) //
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}
}
