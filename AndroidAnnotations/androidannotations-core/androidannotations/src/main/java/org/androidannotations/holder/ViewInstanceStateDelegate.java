/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ViewInstanceStateDelegate extends GeneratedClassHolderDelegate<EComponentHolder> implements HasInstanceState {

	private JVar instanceStateKey;
	private JBlock saveStateMethodBody;
	private JVar saveStateBundleParam;
	private JMethod restoreStateMethod;
	private JVar restoreStateBundleParam;

	public ViewInstanceStateDelegate(EComponentHolder holder) {
		super(holder);
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		if (saveStateMethodBody == null) {
			setSaveStateMethod();
		}
		return saveStateMethodBody;
	}

	@Override
	public JVar getSaveStateBundleParam() {
		if (saveStateBundleParam == null) {
			setSaveStateMethod();
		}
		return saveStateBundleParam;
	}

	private void setSaveStateMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, getClasses().PARCELABLE, "onSaveInstanceState");
		method.annotate(Override.class);

		JMethod saveStateMethod = getGeneratedClass().method(PRIVATE, codeModel().VOID, "saveInstanceState");
		saveStateBundleParam = saveStateMethod.param(getClasses().BUNDLE, "bundle");
		saveStateMethodBody = saveStateMethod.body();

		JBlock methodBody = method.body();
		JVar onSaveSuperInstanceState = methodBody.decl(getClasses().PARCELABLE, "instanceState", _super().invoke("onSaveInstanceState"));

		JVar bundleParam = methodBody.decl(getClasses().BUNDLE, "bundle" + generationSuffix(), _new(getClasses().BUNDLE));
		methodBody.invoke(bundleParam, "putParcelable").arg(getInstanceStateKey()).arg(onSaveSuperInstanceState);

		methodBody.invoke(saveStateMethod).arg(bundleParam);

		methodBody._return(bundleParam);
	}

	@Override
	public JMethod getRestoreStateMethod() {
		if (restoreStateMethod == null) {
			setRestoreStateMethod();
		}
		return restoreStateMethod;
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		if (restoreStateBundleParam == null) {
			setRestoreStateMethod();
		}
		return restoreStateBundleParam;
	}

	private void setRestoreStateMethod() {
		restoreStateMethod = getGeneratedClass().method(PUBLIC, codeModel().VOID, "onRestoreInstanceState");
		restoreStateMethod.annotate(Override.class);
		JVar state = restoreStateMethod.param(getClasses().PARCELABLE, "state");

		JBlock restoreStateMethodBody = restoreStateMethod.body();
		restoreStateBundleParam = restoreStateMethodBody.decl(getClasses().BUNDLE, "bundle" + generationSuffix(), cast(getClasses().BUNDLE, state));
		JVar instanceState = restoreStateMethodBody.decl(getClasses().PARCELABLE, "instanceState", restoreStateBundleParam.invoke("getParcelable").arg(getInstanceStateKey()));
		restoreStateMethodBody.invoke(_super(), "onRestoreInstanceState").arg(instanceState);
	}

	private JVar getInstanceStateKey() {
		if (instanceStateKey == null) {
			instanceStateKey = getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, "INSTANCE_STATE_KEY", lit("instanceState"));
		}
		return instanceStateKey;
	}
}
