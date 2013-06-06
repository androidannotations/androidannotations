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
package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.process.ProcessHolder;

import java.util.HashMap;

import static com.sun.codemodel.JExpr._super;

public class OnActivityResultHolder {

	private EComponentHolder holder;
	private JBlock afterSuperBlock;
	private JSwitch zwitch;
	private JVar requestCodeParam;
	private JVar dataParam;
	private JVar resultCodeParam;
	private HashMap<Integer, JBlock> caseBlocks = new HashMap<Integer, JBlock>();

	public OnActivityResultHolder(EComponentHolder holder) {
		this.holder = holder;
	}

	public JVar getRequestCodeParam() {
		if (requestCodeParam == null) {
			setOnActivityResult();
		}
		return requestCodeParam;
	}

	public JVar getDataParam() {
		if (dataParam == null) {
			setOnActivityResult();
		}
		return dataParam;
	}

	public JVar getResultCodeParam() {
		if (dataParam == null) {
			setOnActivityResult();
		}
		return resultCodeParam;
	}

	public JBlock getCaseBlock(int requestCode) {
		JBlock onActivityResultCaseBlock = caseBlocks.get(requestCode);
		if (onActivityResultCaseBlock == null) {
			onActivityResultCaseBlock = createCaseBlock(requestCode);
			caseBlocks.put(requestCode, onActivityResultCaseBlock);
		}
		return onActivityResultCaseBlock;
	}

	private JBlock createCaseBlock(int requestCode) {
		JCase onActivityResultCase = getSwitch()._case(JExpr.lit(requestCode));
		JBlock onActivityResultCaseBlock = onActivityResultCase.body().block();
		onActivityResultCase.body()._break();
		return onActivityResultCaseBlock;
	}

	public JSwitch getSwitch() {
		if (zwitch == null) {
			setSwitch();
		}
		return zwitch;
	}

	private void setSwitch() {
		zwitch = getAfterSuperBlock()._switch(getRequestCodeParam());
	}

	public JBlock getAfterSuperBlock() {
		if (afterSuperBlock == null) {
			setOnActivityResult();
		}
		return afterSuperBlock;
	}

	private void setOnActivityResult() {
		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, codeModel().VOID, "onActivityResult");
		method.annotate(Override.class);
		requestCodeParam = method.param(codeModel().INT, "requestCode");
		resultCodeParam = method.param(codeModel().INT, "resultCode");
		dataParam = method.param(classes().INTENT, "data");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(requestCodeParam).arg(resultCodeParam).arg(dataParam);
		afterSuperBlock =  body.block();
	}

	private JCodeModel codeModel() {
		return holder.codeModel();
	}

	public ProcessHolder.Classes classes() {
		return holder.classes();
	}
}
