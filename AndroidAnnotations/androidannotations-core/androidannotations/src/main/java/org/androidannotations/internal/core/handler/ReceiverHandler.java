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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.holder.HasActivityLifecycleMethods;
import org.androidannotations.holder.HasReceiverRegistration;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;

public class ReceiverHandler extends CoreBaseAnnotationHandler<HasReceiverRegistration> implements HasParameterHandlers<HasReceiverRegistration> {

	private ExtraHandler extraHandler;

	public ReceiverHandler(AndroidAnnotationsEnvironment environment) {
		super(Receiver.class, environment);
		extraHandler = new ExtraHandler(environment);
	}

	@Override
	public Iterable<AnnotationHandler> getParameterHandlers() {
		return Collections.<AnnotationHandler> singleton(extraHandler);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragmentOrEServiceOrEIntentServiceOrEViewOrEViewGroup(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);

		validatorHelper.param.anyOrder() //
				.type(CanonicalNameConstants.CONTEXT).optional() //
				.type(CanonicalNameConstants.INTENT).optional() //
				.annotatedWith(Receiver.Extra.class).multiple().optional() //
				.validate((ExecutableElement) element, validation);

		validatorHelper.hasNotMultipleAnnotatedMethodWithSameName(element.getEnclosingElement(), validation, Receiver.class);

		coreValidatorHelper.hasRightRegisterAtValueDependingOnEnclosingElement(element, validation);

		coreValidatorHelper.hasSupportV4JarIfLocal(element, validation);
	}

	@Override
	public void process(Element element, HasReceiverRegistration holder) throws Exception {

		String methodName = element.getSimpleName().toString();
		String receiverName = methodName + "Receiver" + generationSuffix();

		Receiver annotation = element.getAnnotation(Receiver.class);
		String[] actions = annotation.actions();
		String[] dataSchemes = annotation.dataSchemes();
		Receiver.RegisterAt registerAt = annotation.registerAt();
		boolean local = annotation.local();

		JFieldVar intentFilterField = holder.getIntentFilterField(new IntentFilterData(actions, dataSchemes, registerAt));
		JFieldVar receiverField = createReceiverField(holder, receiverName, methodName, (ExecutableElement) element);
		registerAndUnregisterReceiver(holder, registerAt, intentFilterField, receiverField, local);
	}

	private JFieldVar createReceiverField(HasReceiverRegistration holder, String receiverName, String methodName, ExecutableElement executableElement) {
		JDefinedClass anonymousReceiverClass = getCodeModel().anonymousClass(getClasses().BROADCAST_RECEIVER);
		JMethod onReceiveMethod = anonymousReceiverClass.method(PUBLIC, getCodeModel().VOID, "onReceive");
		JVar contextVar = onReceiveMethod.param(getClasses().CONTEXT, "context");
		JVar intentVar = onReceiveMethod.param(getClasses().INTENT, "intent");

		JBlock body = onReceiveMethod.body();

		IJExpression receiverRef = holder.getGeneratedClass().staticRef("this");
		JInvocation methodCall = receiverRef.invoke(methodName);
		JVar extras = null;

		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		for (VariableElement param : methodParameters) {
			AbstractJClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType());

			if (extraParamClass.equals(getClasses().CONTEXT)) {
				methodCall.arg(contextVar);
			} else if (extraParamClass.equals(getClasses().INTENT) && param.getAnnotation(Receiver.Extra.class) == null) {
				methodCall.arg(intentVar);
			} else if (param.getAnnotation(Receiver.Extra.class) != null) {
				if (extras == null) {
					extras = body.decl(getClasses().BUNDLE, "extras_", JOp.cond(intentVar.invoke("getExtras").ne(_null()), intentVar.invoke("getExtras"), _new(getClasses().BUNDLE)));
				}
				methodCall.arg(extraHandler.getExtraValue(param, extras, body, onReceiveMethod, anonymousReceiverClass));
			}
		}

		body.add(methodCall);
		IJExpression receiverInit = _new(anonymousReceiverClass);
		return holder.getGeneratedClass().field(PRIVATE | FINAL, getClasses().BROADCAST_RECEIVER, receiverName, receiverInit);
	}

	private void registerAndUnregisterReceiver(HasReceiverRegistration holder, Receiver.RegisterAt registerAt, JFieldVar intentFilterField, JFieldVar receiverField, boolean local) {
		JBlock registerBlock = null;
		JBlock unregisterBlock = null;

		if (holder instanceof HasActivityLifecycleMethods) {
			HasActivityLifecycleMethods activityLifecycleMethods = (HasActivityLifecycleMethods) holder;

			switch (registerAt) {
			case OnCreateOnDestroy:
				registerBlock = activityLifecycleMethods.getOnCreateAfterSuperBlock();
				unregisterBlock = activityLifecycleMethods.getOnDestroyBeforeSuperBlock();
				break;
			case OnStartOnStop:
				registerBlock = activityLifecycleMethods.getOnStartAfterSuperBlock();
				unregisterBlock = activityLifecycleMethods.getOnStopBeforeSuperBlock();
				break;
			case OnResumeOnPause:
				registerBlock = activityLifecycleMethods.getOnResumeAfterSuperBlock();
				unregisterBlock = activityLifecycleMethods.getOnPauseBeforeSuperBlock();
				break;
			}

			if (holder instanceof EFragmentHolder && registerAt == Receiver.RegisterAt.OnAttachOnDetach) {
				EFragmentHolder fragmentHolder = (EFragmentHolder) holder;

				registerBlock = fragmentHolder.getOnAttachAfterSuperBlock();
				unregisterBlock = fragmentHolder.getOnDetachBeforeSuperBlock();
			}

		} else {
			registerBlock = holder.getStartLifecycleAfterSuperBlock();
			unregisterBlock = holder.getEndLifecycleBeforeSuperBlock();
		}

		IJExpression broadcastManager;
		if (local) {
			if (getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.LOCAL_BROADCAST_MANAGER) == null) {
				broadcastManager = getClasses().ANDROIDX_LOCAL_BROADCAST_MANAGER.staticInvoke("getInstance").arg(holder.getContextRef());
			} else {
				broadcastManager = getClasses().LOCAL_BROADCAST_MANAGER.staticInvoke("getInstance").arg(holder.getContextRef());
			}
		} else {
			broadcastManager = holder.getContextRef();
		}

		registerBlock.invoke(broadcastManager, "registerReceiver").arg(receiverField).arg(intentFilterField);
		unregisterBlock.invoke(broadcastManager, "unregisterReceiver").arg(receiverField);
	}

	private static class ExtraHandler extends ExtraParameterHandler {

		ExtraHandler(AndroidAnnotationsEnvironment environment) {
			super(Receiver.Extra.class, Receiver.class, environment);
		}

		@Override
		public String getAnnotationValue(VariableElement parameter) {
			return parameter.getAnnotation(Receiver.Extra.class).value();
		}
	}
}
