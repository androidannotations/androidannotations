/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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

import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;
import org.androidannotations.internal.core.helper.IntentBuilder;
import org.androidannotations.internal.core.helper.ServiceIntentBuilder;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;

public class EServiceHolder extends EComponentHolder implements HasIntentBuilder, HasReceiverRegistration {

	private ServiceIntentBuilder intentBuilder;
	private JDefinedClass intentBuilderClass;
	private ReceiverRegistrationDelegate<EServiceHolder> receiverRegistrationDelegate;
	private JBlock onCreateAfterSuperBlock;

	private JMethod onCreateMethod;
	private JBlock onCreateBody;

	private JMethod onDestroyMethod;
	private JBlock onDestroyBody;
	private JBlock onDestroyBeforeSuperBlock;

	public EServiceHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement, AndroidManifest androidManifest) throws Exception {
		super(environment, annotatedElement);
		receiverRegistrationDelegate = new ReceiverRegistrationDelegate<>(this);
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
		initMethod = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		if (onCreateMethod == null) {
			setOnCreate();
		}
	}

	public JMethod getOnCreate() {
		return onCreateMethod;
	}

	public JBlock getOnCreateBody() {
		return onCreateBody;
	}

	private void setOnCreate() {
		onCreateMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreate");
		onCreateMethod.annotate(Override.class);
		onCreateBody = onCreateMethod.body();
		onCreateBody.invoke(getInit());
		onCreateBody.invoke(JExpr._super(), onCreateMethod);
		onCreateAfterSuperBlock = onCreateBody.blockVirtual();
	}

	public JMethod getOnDestroy() {
		if (onDestroyMethod == null) {
			setOnDestroy();
		}
		return onDestroyMethod;
	}

	public JBlock getOnDestroyBody() {
		if (onDestroyBody == null) {
			setOnDestroy();
		}
		return onDestroyBody;
	}

	private void setOnDestroy() {
		onDestroyMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDestroy");
		onDestroyMethod.annotate(Override.class);
		onDestroyBody = onDestroyMethod.body();
		onDestroyBeforeSuperBlock = onDestroyBody.blockSimple();
		onDestroyBody.invoke(JExpr._super(), onDestroyMethod);
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
	public JFieldVar getIntentFilterField(IntentFilterData intentFilterData) {
		return receiverRegistrationDelegate.getIntentFilterField(intentFilterData);
	}

	@Override
	public JBlock getIntentFilterInitializationBlock(IntentFilterData intentFilterData) {
		return getInitBodyInjectionBlock();
	}

	@Override
	public JBlock getStartLifecycleAfterSuperBlock() {
		if (onCreateAfterSuperBlock == null) {
			setOnCreate();
		}
		return onCreateAfterSuperBlock;
	}

	@Override
	public JBlock getEndLifecycleBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			setOnDestroy();
		}

		return onDestroyBeforeSuperBlock;
	}
}
