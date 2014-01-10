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
package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.HasReceiverRegistration;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class ReceiverHandler extends BaseAnnotationHandler<HasReceiverRegistration> {

	public ReceiverHandler(ProcessingEnvironment processingEnvironment) {
		super(Receiver.class, processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragmentOrEService(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.param.zeroOrOneIntentParameter((ExecutableElement) element, valid);

		validatorHelper.hasNotMultipleAnnotatedMethodWithSameName(element.getEnclosingElement(), valid, Receiver.class);

		validatorHelper.hasRightRegisterAtValueDependingOnEnclosingElement(element, valid);
	}

	@Override
	public void process(Element element, HasReceiverRegistration holder) throws Exception {

		String methodName = element.getSimpleName().toString();
		boolean hasIntentParam = !((ExecutableElement) element).getParameters().isEmpty();
		String receiverName = methodName+"Receiver"+ ModelConstants.GENERATION_SUFFIX;

		Receiver annotation = element.getAnnotation(Receiver.class);
		String[] actions = annotation.actions();
		Receiver.RegisterAt registerAt = annotation.registerAt();

		JFieldVar intentFilterField = holder.getIntentFilterField(actions);
		JFieldVar receiverField = createReceiverField(holder, receiverName, methodName, hasIntentParam);
		registerAndUnregisterReceiver(holder, registerAt, intentFilterField, receiverField);
	}

	private JFieldVar createReceiverField(HasReceiverRegistration holder, String receiverName, String methodName, boolean hasIntentParam) {
		JDefinedClass anonymousReceiverClass = codeModel().anonymousClass(classes().BROADCAST_RECEIVER);
		JMethod onReceiveMethod = anonymousReceiverClass.method(PUBLIC, codeModel().VOID, "onReceive");
		onReceiveMethod.param(classes().CONTEXT, "context");
		JVar intentVar = onReceiveMethod.param(classes().INTENT, "intent");

		JInvocation methodCall = onReceiveMethod.body().invoke(methodName);
		if (hasIntentParam) {
			methodCall.arg(intentVar);
		}

		JExpression receiverInit = _new(anonymousReceiverClass);
		return holder.getGeneratedClass().field(PRIVATE | FINAL, classes().BROADCAST_RECEIVER, receiverName, receiverInit);
	}

	private void registerAndUnregisterReceiver(HasReceiverRegistration holder, Receiver.RegisterAt registerAt, JFieldVar intentFilterField, JFieldVar receiverField) {
		JBlock registerBlock = null, unregisterBlock = null;
		switch (registerAt) {
			case OnCreateOnDestroy:
				registerBlock = holder.getOnCreateAfterSuperBlock();
				unregisterBlock = holder.getOnDestroyBeforeSuperBlock();
				break;
			case OnStartOnStop:
				registerBlock = holder.getOnStartAfterSuperBlock();
				unregisterBlock = holder.getOnStopBeforeSuperBlock();
				break;
			case OnResumeOnPause:
				registerBlock = holder.getOnResumeAfterSuperBlock();
				unregisterBlock = holder.getOnPauseBeforeSuperBlock();
				break;
			case OnAttachOnDetach:
				registerBlock = holder.getOnAttachAfterSuperBlock();
				unregisterBlock = holder.getOnDetachBeforeSuperBlock();
		}

		registerBlock.invoke(holder.getContextRef(), "registerReceiver").arg(receiverField).arg(intentFilterField);
		unregisterBlock.invoke(holder.getContextRef(), "unregisterReceiver").arg(receiverField);
	}
}
