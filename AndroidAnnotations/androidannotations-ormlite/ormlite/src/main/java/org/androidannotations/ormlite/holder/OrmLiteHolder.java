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
package org.androidannotations.ormlite.holder;

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JMod.PRIVATE;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import org.androidannotations.helper.CaseHelper;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.HasSimpleLifecycleMethods;
import org.androidannotations.ormlite.helper.OrmLiteClasses;
import org.androidannotations.plugin.PluginClassHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldVar;

public class OrmLiteHolder extends PluginClassHolder<EComponentHolder> {

	private Map<TypeMirror, JFieldVar> databaseHelperRefs = new HashMap<>();

	public OrmLiteHolder(EComponentHolder holder) {
		super(holder);
	}

	public JFieldVar getDatabaseHelperRef(TypeMirror databaseHelperTypeMirror) {
		JFieldVar databaseHelperRef = databaseHelperRefs.get(databaseHelperTypeMirror);
		if (databaseHelperRef == null) {
			databaseHelperRef = setDatabaseHelperRef(databaseHelperTypeMirror);
			injectReleaseAtEndLifecycle(databaseHelperRef);
		}
		return databaseHelperRef;
	}

	private JFieldVar setDatabaseHelperRef(TypeMirror databaseHelperTypeMirror) {
		AbstractJClass databaseHelperClass = getJClass(databaseHelperTypeMirror.toString());
		String fieldName = CaseHelper.lowerCaseFirst(databaseHelperClass.name()) + ModelConstants.generationSuffix();
		JFieldVar databaseHelperRef = getGeneratedClass().field(PRIVATE, databaseHelperClass, fieldName);
		databaseHelperRefs.put(databaseHelperTypeMirror, databaseHelperRef);

		IJExpression dbHelperClass = databaseHelperClass.dotclass();
		holder().getInitBodyInjectionBlock().assign(databaseHelperRef, //
				getJClass(OrmLiteClasses.OPEN_HELPER_MANAGER).staticInvoke("getHelper").arg(holder().getContextRef()).arg(dbHelperClass));

		return databaseHelperRef;
	}

	private void injectReleaseAtEndLifecycle(JFieldVar databaseHelperRef) {
		if (holder() instanceof HasSimpleLifecycleMethods) {
			JBlock endLifecycleBody = ((HasSimpleLifecycleMethods) holder()).getEndLifecycleBeforeSuperBlock();

			endLifecycleBody.staticInvoke(getJClass(OrmLiteClasses.OPEN_HELPER_MANAGER), "releaseHelper");
			endLifecycleBody.assign(databaseHelperRef, _null());
		}
	}

}
