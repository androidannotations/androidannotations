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
package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JMod.PUBLIC;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;

public class OnCreateOptionMenuFragmentDelegate extends OnCreateOptionMenuDelegate {
	public OnCreateOptionMenuFragmentDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
	}

	void setOnCreateOptionsMenu(CreateOptionAnnotationData createOptionAnnotationData) {
		JMethod method = holder.generatedClass.method(PUBLIC, holder.getCodeModel().VOID, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(getClasses().MENU, "menu");
		if (createOptionAnnotationData != null && createOptionAnnotationData.isCleanBeforeInflate()) {
			methodBody.invoke(onCreateOptionsMenuMenuParam, "clear");
		}
		onCreateOptionsMenuMenuInflaterVar = method.param(getClasses().MENU_INFLATER, "inflater");
		onCreateOptionsMenuMethodBody = methodBody.blockSimple();
		if (createOptionAnnotationData == null || !createOptionAnnotationData.isOverrideParent()) {
			methodBody.invoke(_super(), method).arg(onCreateOptionsMenuMenuParam).arg(onCreateOptionsMenuMenuInflaterVar);
		}

		holder.getInitBody().invoke("setHasOptionsMenu").arg(JExpr.TRUE);
	}
}
