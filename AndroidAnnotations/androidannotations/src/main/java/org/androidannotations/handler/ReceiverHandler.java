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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.Receiver;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasReceiverRegistration;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;

public class ReceiverHandler extends BaseAnnotationHandler<HasReceiverRegistration> {

	private ExtraHandler extraHandler;

	public ReceiverHandler(ProcessingEnvironment processingEnvironment) {
		super(Receiver.class, processingEnvironment);
		extraHandler = new ExtraHandler(processingEnvironment);
	}

	public void register(AnnotationHandlers annotationHandlers) {
		annotationHandlers.add(this);
		annotationHandlers.add(extraHandler);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragmentOrEServiceOrEIntentService(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.param.anyOrder() //
				.type(CanonicalNameConstants.CONTEXT).optional() //
				.type(CanonicalNameConstants.INTENT).optional() //
				.annotatedWith(Receiver.Extra.class).multiple().optional() //
				.validate((ExecutableElement) element, valid);

		validatorHelper.hasNotMultipleAnnotatedMethodWithSameName(element.getEnclosingElement(), valid, Receiver.class);

		validatorHelper.hasRightRegisterAtValueDependingOnEnclosingElement(element, valid);

		validatorHelper.hasSupportV4JarIfLocal(element, valid);
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
		JDefinedClass anonymousReceiverClass = codeModel().anonymousClass(classes().BROADCAST_RECEIVER);
		JMethod onReceiveMethod = anonymousReceiverClass.method(PUBLIC, codeModel().VOID, "onReceive");
		JVar contextVar = onReceiveMethod.param(classes().CONTEXT, "context");
		JVar intentVar = onReceiveMethod.param(classes().INTENT, "intent");

		JBlock body = onReceiveMethod.body();

		JExpression receiverRef = holder.getGeneratedClass().staticRef("this");
		JInvocation methodCall = receiverRef.invoke(methodName);
		JVar extras = null;

		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		for (VariableElement param : methodParameters) {
			JClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType(), holder);

			if (extraParamClass.equals(classes().CONTEXT)) {
				methodCall.arg(contextVar);
			} else if (extraParamClass.equals(classes().INTENT)) {
				methodCall.arg(intentVar);
			} else if (param.getAnnotation(Receiver.Extra.class) != null) {
				if (extras == null) {
					extras = body.decl(classes().BUNDLE, "extras_", JOp.cond(intentVar.invoke("getExtras").ne(_null()), intentVar.invoke("getExtras"), _new(classes().BUNDLE)));
				}
				methodCall.arg(extraHandler.getExtraValue(param, intentVar, extras, body, onReceiveMethod, anonymousReceiverClass, holder));
			}
		}

		body.add(methodCall);
		JExpression receiverInit = _new(anonymousReceiverClass);
		return holder.getGeneratedClass().field(PRIVATE | FINAL, classes().BROADCAST_RECEIVER, receiverName, receiverInit);
	}

	private void registerAndUnregisterReceiver(HasReceiverRegistration holder, Receiver.RegisterAt registerAt, JFieldVar intentFilterField, JFieldVar receiverField, boolean local) {
		JBlock registerBlock = null;
		JBlock unregisterBlock = null;
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

		JExpression broadcastManager;
		if (local) {
			broadcastManager = classes().LOCAL_BROADCAST_MANAGER.staticInvoke("getInstance").arg(holder.getContextRef());
		} else {
			broadcastManager = holder.getContextRef();
		}

		registerBlock.invoke(broadcastManager, "registerReceiver").arg(receiverField).arg(intentFilterField);
		unregisterBlock.invoke(broadcastManager, "unregisterReceiver").arg(receiverField);
	}

	private static class ExtraHandler extends ExtraParameterHandler {

		public ExtraHandler(ProcessingEnvironment processingEnvironment) {
			super(Receiver.Extra.class, Receiver.class, processingEnvironment);
		}

		@Override
		public String getAnnotationValue(VariableElement parameter) {
			return parameter.getAnnotation(Receiver.Extra.class).value();
		}
	}
}
