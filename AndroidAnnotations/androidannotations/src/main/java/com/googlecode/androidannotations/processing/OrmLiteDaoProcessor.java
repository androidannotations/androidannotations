/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JTryBlock;

/**
 * This class generates the code that creates DAOs with ORMLite
 * 
 * @author Johan Poirier <johan.poirier@gmail.com>
 */
public class OrmLiteDaoProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return OrmLiteDao.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);
		Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();
		OrmLiteDao annotation = element.getAnnotation(OrmLiteDao.class);

		// Get model object class
		TypeMirror modelObjectTypeMirror = null;
		try {
			annotation.model();
		} catch (MirroredTypeException mte) {
			modelObjectTypeMirror = mte.getTypeMirror();
		}

		// Get database hleper class
		TypeMirror databaseHelperTypeMirror = null;
		try {
			annotation.helper();
		} catch (MirroredTypeException mte) {
			databaseHelperTypeMirror = mte.getTypeMirror();
		}

		JBlock methodBody = holder.init.body();

		// connection source field
		String connectionSourceRef = "connection_source_";
		try {
			holder.eBean.field(PRIVATE, classes.CONNECTION_SOURCE, connectionSourceRef);
		} catch (IllegalArgumentException e) {
			// connection_source_ already generated
		}

		// context field
		String contextRef = "context_";
		JFieldVar contextField;
		try {
			contextField = holder.eBean.field(PRIVATE, classes.CONTEXT, contextRef);
			methodBody._if(JExpr._this()._instanceof(classes.CONTEXT))._then().assign(contextField, JExpr._this());
		} catch (IllegalArgumentException e) {
			// context_ already generated
		}

		// get connection source
		JExpression dbHelperExpr = holder.refClass(databaseHelperTypeMirror.toString()).dotclass();
		methodBody._if(JExpr.ref(connectionSourceRef).eq(JExpr._null()))._then().assign(ref(connectionSourceRef), holder.refClass(CanonicalNameConstants.OPEN_HELPER_MANAGER).staticInvoke("getHelper").arg(ref(contextRef)).arg(dbHelperExpr).invoke("getConnectionSource"));

		// create dao from dao manager
		JTryBlock tryBlock = methodBody._try();
		tryBlock.body().assign(ref(fieldName), holder.refClass(CanonicalNameConstants.DAO_MANAGER).staticInvoke("createDao").arg(JExpr.ref(connectionSourceRef)).arg(holder.refClass(modelObjectTypeMirror.toString()).dotclass()));
		tryBlock._catch(classes.SQL_EXCEPTION).body().directStatement("android.util.Log.d(\"AndroidAnnotations\", _x.getMessage()); _x.printStackTrace();");
	}
}
