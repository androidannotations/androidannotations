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

import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

/**
 * This class generates the code that creates DAOs with ORMLite
 */
public class OrmLiteDaoProcessor implements DecoratingElementProcessor {

	private static final String DATABASE_HELPER_FIELD_NAME = "helper_";
	private TargetAnnotationHelper helper;

	public OrmLiteDaoProcessor(ProcessingEnvironment processingEnv) {
		helper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public String getTarget() {
		return OrmLiteDao.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		TypeMirror modelObjectTypeMirror = helper.extractAnnotationParameter(element, "model");

		TypeMirror databaseHelperTypeMirror = helper.extractAnnotationParameter(element, "helper");

		// database helper field
		boolean databaseHelperInjected = holder.generatedClass.fields().containsKey(DATABASE_HELPER_FIELD_NAME);

		JBlock initBody = holder.initBody;

		JFieldVar databaseHelperRef;
		if (databaseHelperInjected) {
			databaseHelperRef = holder.generatedClass.fields().get(DATABASE_HELPER_FIELD_NAME);
		} else {
			databaseHelperRef = holder.generatedClass.field(PRIVATE, holder.refClass(databaseHelperTypeMirror.toString()), DATABASE_HELPER_FIELD_NAME);

			// get database helper instance
			JExpression dbHelperClass = holder.refClass(databaseHelperTypeMirror.toString()).dotclass();

			initBody.assign(databaseHelperRef, //
					classes.OPEN_HELPER_MANAGER //
							.staticInvoke("getHelper") //
							.arg(holder.contextRef) //
							.arg(dbHelperClass));
		}

		// create dao from database helper
		JTryBlock tryBlock = initBody._try();

		JExpression modelClass = holder.refClass(modelObjectTypeMirror.toString()).dotclass();
		tryBlock.body().assign(ref(fieldName), //
				databaseHelperRef.invoke("getDao"). //
						arg(modelClass));

		JCatchBlock catchBlock = tryBlock._catch(classes.SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(classes.LOG, "e") //
				.arg(holder.generatedClass.name()) //
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}
}
