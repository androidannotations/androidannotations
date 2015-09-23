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

import static com.helger.jcodemodel.JExpr.TRUE;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.PUBLIC;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class OnCreateOptionMenuDelegate extends GeneratedClassHolderDelegate<EComponentWithViewSupportHolder>implements HasOptionsMenu {

	JBlock onCreateOptionsMenuMethodBody;
	JVar onCreateOptionsMenuMenuInflaterVar;
	JVar onCreateOptionsMenuMenuParam;
	JVar onOptionsItemSelectedItem;
	JVar onOptionsItemSelectedItemId;
	JBlock onOptionsItemSelectedMiddleBlock;

	public OnCreateOptionMenuDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
	}

	void setOnCreateOptionsMenu(CreateOptionAnnotationData createOptionAnnotationData) {
		JMethod method = holder.generatedClass.method(PUBLIC, holder.getCodeModel().BOOLEAN, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(getClasses().MENU, "menu");
		if (createOptionAnnotationData != null && createOptionAnnotationData.isCleanBeforeInflate()) {
			methodBody.invoke(onCreateOptionsMenuMenuParam, "clear");
		}
		onCreateOptionsMenuMenuInflaterVar = methodBody.decl(getClasses().MENU_INFLATER, "menuInflater", invoke("getMenuInflater"));
		onCreateOptionsMenuMethodBody = methodBody.blockSimple();

		if (createOptionAnnotationData != null && createOptionAnnotationData.isOverrideParent()) {
			methodBody._return(TRUE);
		} else {
			methodBody._return(_super().invoke(method).arg(onCreateOptionsMenuMenuParam));
		}
	}

	private void setOnOptionsItemSelected() {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, holder.getCodeModel().BOOLEAN, "onOptionsItemSelected");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onOptionsItemSelectedItem = method.param(getClasses().MENU_ITEM, "item");
		onOptionsItemSelectedItemId = methodBody.decl(holder.getCodeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedMiddleBlock = methodBody.blockSimple();

		methodBody._return(invoke(_super(), method).arg(onOptionsItemSelectedItem));
	}

	@Override
	public JBlock getOnCreateOptionsMenuMethodBody(CreateOptionAnnotationData createOptionAnnotationData) {
		if (onCreateOptionsMenuMethodBody == null) {
			setOnCreateOptionsMenu(createOptionAnnotationData);
		}
		return onCreateOptionsMenuMethodBody;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuInflaterVar(CreateOptionAnnotationData createOptionAnnotationData) {
		if (onCreateOptionsMenuMenuInflaterVar == null) {
			setOnCreateOptionsMenu(createOptionAnnotationData);
		}
		return onCreateOptionsMenuMenuInflaterVar;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuParam(CreateOptionAnnotationData createOptionAnnotationData) {
		if (onCreateOptionsMenuMenuParam == null) {
			setOnCreateOptionsMenu(createOptionAnnotationData);
		}
		return onCreateOptionsMenuMenuParam;
	}

	@Override
	public JVar getOnOptionsItemSelectedItem() {
		if (onOptionsItemSelectedItem == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItem;
	}

	@Override
	public JVar getOnOptionsItemSelectedItemId() {
		if (onOptionsItemSelectedItemId == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItemId;
	}

	@Override
	public JBlock getOnOptionsItemSelectedMiddleBlock() {
		if (onOptionsItemSelectedMiddleBlock == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedMiddleBlock;
	}

	public static class CreateOptionAnnotationData {

		private final boolean overrideParent;
		private final boolean cleanBeforeInflate;

		public CreateOptionAnnotationData(boolean overrideParent, boolean cleanBeforeInflate) {
			this.overrideParent = overrideParent;
			this.cleanBeforeInflate = cleanBeforeInflate;
		}

		public boolean isOverrideParent() {
			return overrideParent;
		}

		public boolean isCleanBeforeInflate() {
			return cleanBeforeInflate;
		}
	}

}
