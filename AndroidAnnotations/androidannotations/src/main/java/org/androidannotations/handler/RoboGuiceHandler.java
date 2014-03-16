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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.invoke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.holder.RoboGuiceHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class RoboGuiceHandler extends BaseAnnotationHandler<EActivityHolder> {

    private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public RoboGuiceHandler(ProcessingEnvironment processingEnvironment) {
		super(RoboGuice.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasEActivity(element, validatedElements, valid);

		validatorHelper.hasRoboGuiceJars(element, valid);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {
		RoboGuiceHolder roboGuiceHolder = holder.getRoboGuiceHolder();

		holder.getGeneratedClass()._implements(classes().INJECTOR_PROVIDER);

		JFieldVar scope = roboGuiceHolder.getScopeField();
		JFieldVar eventManager = roboGuiceHolder.getEventManagerField();
		JMethod getInjector = roboGuiceHolder.getGetInjector();
		listenerFields(element, holder);

		beforeCreateMethod(holder, scope, eventManager, getInjector);
		afterSetContentView(holder, scope, eventManager);
		onRestartMethod(roboGuiceHolder, scope, eventManager);
		onStartMethod(roboGuiceHolder, scope, eventManager);
		onResumeMethod(roboGuiceHolder, scope, eventManager);
		onPauseMethod(roboGuiceHolder, eventManager);
		onNewIntentMethod(roboGuiceHolder, scope, eventManager);
		onStopMethod(roboGuiceHolder, scope, eventManager);
		onDestroyMethod(roboGuiceHolder, scope, eventManager);
		onConfigurationChangedMethod(roboGuiceHolder, eventManager);
		onContentChangedMethod(roboGuiceHolder, eventManager);
		onActivityResultMethod(roboGuiceHolder, scope, eventManager);
	}

	private void listenerFields(Element element, EActivityHolder holder) {
		List<String> listenerClasses = extractListenerClasses(element);
		if (listenerClasses.size() > 0) {
			int i = 1;
			for (String listenerClassName : listenerClasses) {
				JClass listenerClass = refClass(listenerClassName);
				JFieldVar listener = holder.getGeneratedClass().field(JMod.PRIVATE, listenerClass, "listener" + i + "_");
				listener.annotate(SuppressWarnings.class).param("value", "unused");
				listener.annotate(classes().INJECT);
				i++;
			}
		}
	}

	private List<String> extractListenerClasses(Element activityElement) {

		List<? extends AnnotationMirror> annotationMirrors = activityElement.getAnnotationMirrors();

		String annotationName = RoboGuice.class.getName();
		AnnotationValue action;
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationName.equals(annotationMirror.getAnnotationType().toString())) {
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					if ("value".equals(entry.getKey().getSimpleName().toString())) {
						action = entry.getValue();

						@SuppressWarnings("unchecked")
						List<Object> values = (List<Object>) action.getValue();

						List<String> listenerClasses = new ArrayList<String>();

						for (Object value : values) {
							listenerClasses.add(value.toString());
						}
						return listenerClasses;

					}
				}
			}
		}
		return new ArrayList<String>(0);
	}

	private void beforeCreateMethod(EActivityHolder holder, JFieldVar scope, JFieldVar eventManager, JMethod getInjector) {
		JBlock body = holder.getInitBody();
		JVar injector = body.decl(classes().INJECTOR, "injector_", invoke(getInjector));
		body.assign(scope, invoke(injector, "getInstance").arg(classes().CONTEXT_SCOPE.dotclass()));
		body.invoke(scope, "enter").arg(_this());
		body.invoke(injector, "injectMembers").arg(_this());
		body.assign(eventManager, invoke(injector, "getInstance").arg(classes().EVENT_MANAGER.dotclass()));
		fireEvent(eventManager, body, classes().ON_CREATE_EVENT, holder.getInitSavedInstanceParam());
	}

	private void afterSetContentView(EActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onViewChanged = holder.getOnViewChangedBody();
		onViewChanged.invoke(scope, "injectViews");
		fireEvent(eventManager, onViewChanged, classes().ON_CONTENT_VIEW_AVAILABLE_EVENT);
	}

	private void onRestartMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onRestartBeforeSuperBlock = holder.getOnRestartBeforeSuperBlock();
		onRestartBeforeSuperBlock.invoke(scope, "enter").arg(_this());
		JBlock onRestartAfterSuperBlock = holder.getOnRestartAfterSuperBlock();
		fireEvent(eventManager, onRestartAfterSuperBlock, classes().ON_RESTART_EVENT);
	}

	private void onStartMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onStartBeforeSuperBlock = holder.getOnStartBeforeSuperBlock();
		onStartBeforeSuperBlock.invoke(scope, "enter").arg(_this());
		JBlock onStartAfterSuperBlock = holder.getOnStartAfterSuperBlock();
		fireEvent(eventManager, onStartAfterSuperBlock, classes().ON_START_EVENT);
	}

	private void onResumeMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onResumeBeforeSuperBlock = holder.getOnResumeBeforeSuperBlock();
		onResumeBeforeSuperBlock.invoke(scope, "enter").arg(_this());
		JBlock onResumeAfterSuperBlock = holder.getOnResumeAfterSuperBlock();
		fireEvent(eventManager, onResumeAfterSuperBlock, classes().ON_RESUME_EVENT);
	}

	private void onPauseMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onPauseAfterSuperBlock = holder.getOnPauseAfterSuperBlock();
		fireEvent(eventManager, onPauseAfterSuperBlock, classes().ON_PAUSE_EVENT);
	}

	private void onNewIntentMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onNewIntentAfterSuperBlock = holder.getOnNewIntentAfterSuperBlock();
		onNewIntentAfterSuperBlock.invoke(scope, "enter").arg(_this());
		fireEvent(eventManager, onNewIntentAfterSuperBlock, classes().ON_NEW_INTENT_EVENT);
	}

	private void onStopMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onStopBlock = new JBlock(false, false);
		onStopBlock.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = onStopBlock._try();
		fireEvent(eventManager, tryBlock.body(), classes().ON_STOP_EVENT);
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(_super(), "onStop");

        JMethod onStop = holder.getOnStop();
        codeModelHelper.replaceSuperCall(onStop, onStopBlock);
	}

	private void onDestroyMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JBlock onDestroyBlock = new JBlock(false, false);
		onDestroyBlock.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = onDestroyBlock._try();
		fireEvent(eventManager, tryBlock.body(), classes().ON_DESTROY_EVENT);
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(eventManager, "clear").arg(_this());
		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(scope, "dispose").arg(_this());
		finallyBody.invoke(_super(), "onDestroy");

        JMethod onDestroy = holder.getOnDestroy();
        codeModelHelper.replaceSuperCall(onDestroy, onDestroyBlock);
	}

	private void onConfigurationChangedMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JVar currentConfig = holder.getCurrentConfig();
		JBlock onConfigurationChangedAfterSuperBlock = holder.getOnConfigurationChangedAfterSuperBlock();
		JExpression newConfig = holder.getNewConfig();
		fireEvent(eventManager, onConfigurationChangedAfterSuperBlock, classes().ON_CONFIGURATION_CHANGED_EVENT, currentConfig, newConfig);
	}

	private void onContentChangedMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onContentChangedAfterSuperBlock = holder.getOnContentChangedAfterSuperBlock();
		fireEvent(eventManager, onContentChangedAfterSuperBlock, classes().ON_CONTENT_CHANGED_EVENT);
	}

	private void onActivityResultMethod(RoboGuiceHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onActivityResultAfterSuperBlock = holder.getOnActivityResultAfterSuperBlock();
		JVar requestCode = holder.getRequestCode();
		JVar resultCode = holder.getResultCode();
		JVar data = holder.getData();

		onActivityResultAfterSuperBlock.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = onActivityResultAfterSuperBlock._try();
		fireEvent(eventManager, tryBlock.body(), classes().ON_ACTIVITY_RESULT_EVENT, requestCode, resultCode, data);

		JBlock finallyBody = tryBlock._finally();
		finallyBody.invoke(scope, "exit").arg(_this());
	}

	private void fireEvent(JFieldVar eventManager, JBlock body, JClass eventClass, JExpression... eventArguments) {
		JInvocation newEvent = _new(eventClass);
		for (JExpression eventArgument : eventArguments) {
			newEvent.arg(eventArgument);
		}
		body.invoke(eventManager, "fire").arg(newEvent);
	}
}
