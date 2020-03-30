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
package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._super;

import java.util.HashMap;
import java.util.Map;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCase;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;

public class OnActivityResultDelegate extends GeneratedClassHolderDelegate<EComponentHolder> {

	private JMethod method;
	private JBlock afterSuperBlock;
	private JSwitch zwitch;
	private JVar requestCodeParam;
	private JVar dataParam;
	private JVar resultCodeParam;
	private Map<Integer, JBlock> caseBlocks = new HashMap<>();

	public OnActivityResultDelegate(EComponentHolder holder) {
		super(holder);
	}

	public JMethod getMethod() {
		if (method == null) {
			setOnActivityResult();
		}
		return method;
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
		JBlock onActivityResultCaseBlock = onActivityResultCase.body().blockSimple();
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
		method = holder.getGeneratedClass().method(JMod.PUBLIC, codeModel().VOID, "onActivityResult");
		method.annotate(Override.class);
		requestCodeParam = method.param(codeModel().INT, "requestCode");
		resultCodeParam = method.param(codeModel().INT, "resultCode");
		dataParam = method.param(getClasses().INTENT, "data");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(requestCodeParam).arg(resultCodeParam).arg(dataParam);
		afterSuperBlock = body.blockSimple();
	}
}
