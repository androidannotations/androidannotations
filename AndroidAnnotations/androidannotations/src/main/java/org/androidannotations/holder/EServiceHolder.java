/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IntentBuilder;
import org.androidannotations.helper.ServiceIntentBuilder;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class EServiceHolder extends EComponentHolder implements HasIntentBuilder, HasReceiverRegistration {

    private ServiceIntentBuilder intentBuilder;
	private JDefinedClass intentBuilderClass;
	private ReceiverRegistrationHolder receiverRegistrationHolder;
	private JBlock onDestroyBeforeSuperBlock;

	public EServiceHolder(ProcessHolder processHolder, TypeElement annotatedElement, AndroidManifest androidManifest) throws Exception {
		super(processHolder, annotatedElement);
		receiverRegistrationHolder = new ReceiverRegistrationHolder(this);
        intentBuilder = new ServiceIntentBuilder(this, androidManifest);
        intentBuilder.build();
	}

    @Override
    public IntentBuilder getIntentBuilder() {
        return intentBuilder;
    }

    @Override
	protected void setContextRef() {
		contextRef = _this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		setOnCreate();
	}

	private void setOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JBlock onCreateBody = onCreate.body();
		onCreateBody.invoke(getInit());
		onCreateBody.invoke(JExpr._super(), onCreate);
	}

	private void setOnDestroy() {
		JMethod onDestroy = generatedClass.method(PUBLIC, codeModel().VOID, "onDestroy");
		onDestroy.annotate(Override.class);
		JBlock onDestroyBody = onDestroy.body();
		onDestroyBeforeSuperBlock = onDestroyBody.block();
		onDestroyBody.invoke(JExpr._super(), onDestroy);
	}

	@Override
	public void setIntentBuilderClass(JDefinedClass intentBuilderClass) {
		this.intentBuilderClass = intentBuilderClass;
	}

	@Override
	public JDefinedClass getIntentBuilderClass() {
		return intentBuilderClass;
	}

	@Override
	public JFieldVar getIntentFilterField(String[] actions) {
		return receiverRegistrationHolder.getIntentFilterField(actions);
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		return getInitBody();
	}

	@Override
	public JBlock getOnDestroyBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			setOnDestroy();
		}
		return onDestroyBeforeSuperBlock;
	}

	@Override
	public JBlock getOnStartAfterSuperBlock() {
		return receiverRegistrationHolder.getOnStartAfterSuperBlock();
	}

	@Override
	public JBlock getOnStopBeforeSuperBlock() {
		return receiverRegistrationHolder.getOnStopBeforeSuperBlock();
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		return receiverRegistrationHolder.getOnAttachAfterSuperBlock();
	}

	@Override
	public JBlock getOnPauseBeforeSuperBlock() {
		return receiverRegistrationHolder.getOnPauseBeforeSuperBlock();
	}

	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		return receiverRegistrationHolder.getOnAttachAfterSuperBlock();
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		return receiverRegistrationHolder.getOnDetachBeforeSuperBlock();
	}
}
