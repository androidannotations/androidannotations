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

import java.lang.annotation.Annotation;

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

	private static final String CONNECTION_SOURCE_FIELD_NAME = "connectionSource_";
	private TargetAnnotationHelper helper;

	public OrmLiteDaoProcessor(ProcessingEnvironment processingEnv) {
		helper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OrmLiteDao.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		TypeMirror modelObjectTypeMirror = helper.extractAnnotationParameter(element, "model");

		TypeMirror databaseHelperTypeMirror = helper.extractAnnotationParameter(element, "helper");

		// connection source field
		boolean connectionSourceInjected = holder.generatedClass.fields().containsKey(CONNECTION_SOURCE_FIELD_NAME);

		JBlock initBody = holder.initBody;

		JFieldVar connectionSourceRef;
		if (connectionSourceInjected) {
			connectionSourceRef = holder.generatedClass.fields().get(CONNECTION_SOURCE_FIELD_NAME);
		} else {
			connectionSourceRef = holder.generatedClass.field(PRIVATE, classes.CONNECTION_SOURCE, CONNECTION_SOURCE_FIELD_NAME);

			// get connection source
			JExpression dbHelperClass = holder.refClass(databaseHelperTypeMirror.toString()).dotclass();

			initBody.assign(connectionSourceRef, //
					classes.OPEN_HELPER_MANAGER //
							.staticInvoke("getHelper") //
							.arg(holder.contextRef) //
							.arg(dbHelperClass) //
							.invoke("getConnectionSource"));
		}

		// create dao from dao manager
		JTryBlock tryBlock = initBody._try();

		JExpression modelClass = holder.refClass(modelObjectTypeMirror.toString()).dotclass();
		tryBlock.body().assign(ref(fieldName), //
				classes.DAO_MANAGER //
						.staticInvoke("createDao") //
						.arg(connectionSourceRef) //
						.arg(modelClass));

		JCatchBlock catchBlock = tryBlock._catch(classes.SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(classes.LOG, "e") //
				.arg(holder.generatedClass.name()) //
				.arg("Could not create DAO") //
				.arg(exception);
	}
}
