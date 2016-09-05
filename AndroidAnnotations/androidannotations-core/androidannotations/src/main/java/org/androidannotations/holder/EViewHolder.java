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

import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class EViewHolder extends EComponentWithViewSupportHolder implements HasInstanceState, HasReceiverRegistration {

	protected static final String ALREADY_INFLATED_COMMENT = "" // +
			+ "The alreadyInflated_ hack is needed because of an Android bug\n" // +
			+ "which leads to infinite calls of onFinishInflate()\n" //
			+ "when inflating a layout with a parent and using\n" //
			+ "the <merge /> tag.";

	private static final String SUPPRESS_WARNING_COMMENT = "" //
			+ "We use @SuppressWarning here because our java code\n" //
			+ "generator doesn't know that there is no need\n" //
			+ "to import OnXXXListeners from View as we already\n" //
			+ "are in a View.";

	private JMethod onAttachedToWindowMethod;
	private JBlock onAttachedToWindowAfterSuperBlock;
	private JMethod onDetachedFromWindowMethod;
	private JBlock onDetachedFromWindowBeforeSuperBlock;
	private ReceiverRegistrationDelegate<EViewHolder> receiverRegistrationDelegate;
	private ViewInstanceStateDelegate instanceStateDelegate;
	protected JBlock initBody;
	protected JMethod onFinishInflate;
	protected JFieldVar alreadyInflated;

	public EViewHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		addSuppressWarning();
		createConstructorAndBuilder();
		receiverRegistrationDelegate = new ReceiverRegistrationDelegate<>(this);
		instanceStateDelegate = new ViewInstanceStateDelegate(this);
	}

	private void addSuppressWarning() {
		generatedClass.javadoc().append(SUPPRESS_WARNING_COMMENT);

		codeModelHelper.addSuppressWarnings(getGeneratedClass(), "unused");
	}

	private void createConstructorAndBuilder() {
		List<ExecutableElement> constructors = new ArrayList<>();
		for (Element e : annotatedElement.getEnclosedElements()) {
			if (e.getKind() == CONSTRUCTOR) {
				constructors.add((ExecutableElement) e);
			}
		}

		for (ExecutableElement userConstructor : constructors) {
			JMethod copyConstructor = generatedClass.constructor(PUBLIC);
			JMethod staticHelper = generatedClass.method(PUBLIC | STATIC, generatedClass._extends(), "build");

			codeModelHelper.generify(staticHelper, getAnnotatedElement());

			JBlock body = copyConstructor.body();
			JInvocation superCall = body.invoke("super");
			AbstractJClass narrowedGeneratedClass = narrow(generatedClass);

			JInvocation newInvocation = JExpr._new(narrowedGeneratedClass);
			for (VariableElement param : userConstructor.getParameters()) {
				String paramName = param.getSimpleName().toString();
				AbstractJClass paramType = codeModelHelper.typeMirrorToJClass(param.asType());
				copyConstructor.param(paramType, paramName);
				staticHelper.param(paramType, paramName);
				superCall.arg(JExpr.ref(paramName));
				newInvocation.arg(JExpr.ref(paramName));
			}

			JVar newCall = staticHelper.body().decl(narrowedGeneratedClass, "instance", newInvocation);
			staticHelper.body().invoke(newCall, getOnFinishInflate());
			staticHelper.body()._return(newCall);
			body.invoke(getInit());
		}
	}

	@Override
	protected void setContextRef() {
		contextRef = invoke("getContext");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		viewNotifierHelper.wrapInitWithNotifier();
	}

	@Override
	public JBlock getInitBody() {
		if (initBody == null) {
			setInit();
		}
		return initBody;
	}

	public void setInitBody(JBlock initBody) {
		this.initBody = initBody;
	}

	public JMethod getOnFinishInflate() {
		if (onFinishInflate == null) {
			setOnFinishInflate();
		}
		return onFinishInflate;
	}

	protected void setOnFinishInflate() {
		onFinishInflate = generatedClass.method(PUBLIC, getCodeModel().VOID, "onFinishInflate");
		onFinishInflate.annotate(Override.class);
		onFinishInflate.javadoc().append(ALREADY_INFLATED_COMMENT.replaceAll("alreadyInflated_", "alreadyInflated" + generationSuffix()));

		JBlock ifNotInflated = onFinishInflate.body()._if(getAlreadyInflated().not())._then();
		ifNotInflated.assign(getAlreadyInflated(), JExpr.TRUE);

		getInit();
		viewNotifierHelper.invokeViewChanged(ifNotInflated);

		onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");
	}

	public JFieldVar getAlreadyInflated() {
		if (alreadyInflated == null) {
			setAlreadyInflated();
		}
		return alreadyInflated;
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		return receiverRegistrationDelegate.getOnCreateAfterSuperBlock();
	}

	@Override
	public JBlock getOnDestroyBeforeSuperBlock() {
		return receiverRegistrationDelegate.getOnDestroyBeforeSuperBlock();
	}

	@Override
	public JBlock getOnStartAfterSuperBlock() {
		return receiverRegistrationDelegate.getOnStartAfterSuperBlock();
	}

	@Override
	public JBlock getOnStopBeforeSuperBlock() {
		return receiverRegistrationDelegate.getOnStopBeforeSuperBlock();
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		return receiverRegistrationDelegate.getOnResumeAfterSuperBlock();
	}

	@Override
	public JBlock getOnPauseBeforeSuperBlock() {
		return receiverRegistrationDelegate.getOnPauseBeforeSuperBlock();
	}

	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		return getOnAttachedToWindowAfterSuperBlock();
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		return getOnDetachedToWindowBeforeSuperBlock();
	}

	@Override
	public JFieldVar getIntentFilterField(ReceiverRegistrationDelegate.IntentFilterData intentFilterData) {
		return receiverRegistrationDelegate.getIntentFilterField(intentFilterData);
	}

	@Override
	public JBlock getIntentFilterInitializationBlock(ReceiverRegistrationDelegate.IntentFilterData intentFilterData) {
		return getInitBodyInjectionBlock();
	}

	protected JBlock getOnAttachedToWindowAfterSuperBlock() {
		if (onAttachedToWindowAfterSuperBlock == null) {
			setOnAttachedToWindow();
		}
		return onAttachedToWindowAfterSuperBlock;
	}

	protected JBlock getOnDetachedToWindowBeforeSuperBlock() {
		if (onDetachedFromWindowBeforeSuperBlock == null) {
			setOnDetachedFromWindow();
		}
		return onDetachedFromWindowBeforeSuperBlock;
	}

	private void setAlreadyInflated() {
		alreadyInflated = generatedClass.field(PRIVATE, getCodeModel().BOOLEAN, "alreadyInflated" + generationSuffix(), JExpr.FALSE);
	}

	private void setOnAttachedToWindow() {
		onAttachedToWindowMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onAttachedToWindow");
		onAttachedToWindowMethod.annotate(Override.class);
		JBlock body = onAttachedToWindowMethod.body();
		body.invoke(_super(), onAttachedToWindowMethod);
		onAttachedToWindowAfterSuperBlock = body.blockSimple();
	}

	private void setOnDetachedFromWindow() {
		onDetachedFromWindowMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onDetachedFromWindow");
		onDetachedFromWindowMethod.annotate(Override.class);
		JBlock body = onDetachedFromWindowMethod.body();
		onDetachedFromWindowBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), onAttachedToWindowMethod);
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		return instanceStateDelegate.getSaveStateMethodBody();
	}

	@Override
	public JVar getSaveStateBundleParam() {
		return instanceStateDelegate.getSaveStateBundleParam();
	}

	@Override
	public JMethod getRestoreStateMethod() {
		return instanceStateDelegate.getRestoreStateMethod();
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		return instanceStateDelegate.getRestoreStateBundleParam();
	}
}
