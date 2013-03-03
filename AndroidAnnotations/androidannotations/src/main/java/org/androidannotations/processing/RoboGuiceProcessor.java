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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class RoboGuiceProcessor implements DecoratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return RoboGuice.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		holder.generatedClass._implements(holder.classes().INJECTOR_PROVIDER);

		// Fields
		JFieldVar scope = scopeField(holder);
		JFieldVar eventManager = eventManagerField(holder);
		listenerFields(element, holder);

		// Methods
		afterSetContentView(codeModel, holder, scope, eventManager);
		onRestartMethod(codeModel, holder, scope, eventManager);
		onStartMethod(codeModel, holder, scope, eventManager);
		onResumeMethod(codeModel, holder, scope, eventManager);
		onPauseMethod(codeModel, holder, scope, eventManager);
		onNewIntentMethod(codeModel, holder, scope, eventManager);
		onStopMethod(codeModel, holder, scope, eventManager);
		onDestroyMethod(codeModel, holder, scope, eventManager);
		onConfigurationChangedMethod(codeModel, holder, scope, eventManager);
		onContentChangedMethod(codeModel, holder, scope, eventManager);
		onActivityResultMethod(codeModel, holder, scope, eventManager);

		JMethod getInjectorMethod = getInjectorMethod(holder);

		beforeCreateMethod(holder, scope, eventManager, getInjectorMethod);

	}

	private JMethod getInjectorMethod(EBeanHolder holder) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, holder.classes().INJECTOR, "getInjector");
		method.annotate(Override.class);
		JExpression castApplication = cast(holder.classes().INJECTOR_PROVIDER, invoke("getApplication"));
		method.body()._return(castApplication.invoke("getInjector"));
		return method;
	}

	private void onRestartMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onRestart");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, holder.classes().ON_RESTART_EVENT);
	}

	private void onStartMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onStart");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, holder.classes().ON_START_EVENT);
	}

	private void onResumeMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onResume");
		method.annotate(Override.class);
		holder.onResumeBlock = method.body();
		holder.onResumeBlock.invoke(scope, "enter").arg(_this());
		holder.onResumeBlock.invoke(_super(), method);
		fireEvent(holder, eventManager, holder.onResumeBlock, holder.classes().ON_RESUME_EVENT);
	}

	private void onPauseMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onPause");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, holder.classes().ON_PAUSE_EVENT);
	}

	private void onNewIntentMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onNewIntent");
		method.annotate(Override.class);
		JVar intent = method.param(holder.classes().INTENT, "intent");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(intent);
		body.invoke(scope, "enter").arg(_this());
		fireEvent(holder, eventManager, body, holder.classes().ON_NEW_INTENT_EVENT);
	}

	private void fireEvent(EBeanHolder holder, JFieldVar eventManager, JBlock body, JClass eventClass, JExpression... eventArguments) {
		JInvocation newEvent = _new(eventClass);
		for (JExpression eventArgument : eventArguments) {
			newEvent.arg(eventArgument);
		}
		body.invoke(eventManager, "fire").arg(newEvent);
	}

	private void onStopMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onStop");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = body._try();
		fireEvent(holder, eventManager, tryBlock.body(), holder.classes().ON_STOP_EVENT);
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(_super(), method);
	}

	private void onDestroyMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onDestroy");
		method.annotate(Override.class);
		holder.onDestroyBlock = method.body();
		holder.onDestroyBlock.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = holder.onDestroyBlock._try();
		fireEvent(holder, eventManager, tryBlock.body(), holder.classes().ON_DESTROY_EVENT);
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(eventManager, "clear").arg(_this());
		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(scope, "dispose").arg(_this());
		finallyBody.invoke(_super(), method);
	}

	private void onConfigurationChangedMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onConfigurationChanged");
		method.annotate(Override.class);
		JClass configurationClass = holder.classes().CONFIGURATION;
		JVar newConfig = method.param(configurationClass, "newConfig");

		JBlock body = method.body();
		JVar currentConfig = body.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));

		body.invoke(_super(), method).arg(newConfig);
		fireEvent(holder, eventManager, body, holder.classes().ON_CONFIGURATION_CHANGED_EVENT, currentConfig, newConfig);
	}

	private void onContentChangedMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onContentChanged");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, holder.classes().ON_CONTENT_CHANGED_EVENT);
	}

	private void onActivityResultMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onActivityResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(codeModel.INT, "requestCode");
		JVar resultCode = method.param(codeModel.INT, "resultCode");
		JVar data = method.param(holder.classes().INTENT, "data");

		JBlock body = method.body();

		body.invoke(_super(), method).arg(requestCode).arg(resultCode).arg(data);

		body.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = body._try();
		fireEvent(holder, eventManager, tryBlock.body(), holder.classes().ON_ACTIVITY_RESULT_EVENT, requestCode, resultCode, data);

		JBlock finallyBody = tryBlock._finally();
		finallyBody.invoke(scope, "exit").arg(_this());
	}

	private void afterSetContentView(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onViewChanged = holder.onViewChanged().body();
		onViewChanged.invoke(scope, "injectViews");
		fireEvent(holder, eventManager, onViewChanged, holder.classes().ON_CONTENT_VIEW_AVAILABLE_EVENT);
	}

	private JFieldVar eventManagerField(EBeanHolder holder) {
		JFieldVar eventManager = holder.generatedClass.field(JMod.PRIVATE, holder.classes().EVENT_MANAGER, "eventManager_");
		return eventManager;
	}

	private JFieldVar scopeField(EBeanHolder holder) {
		JFieldVar scope = holder.generatedClass.field(JMod.PRIVATE, holder.classes().CONTEXT_SCOPE, "scope_");
		return scope;
	}

	private void listenerFields(Element element, EBeanHolder holder) {
		List<String> listenerClasses = extractListenerClasses(element);
		if (listenerClasses.size() > 0) {
			int i = 1;
			for (String listenerClassName : listenerClasses) {
				JClass listenerClass = holder.refClass(listenerClassName);
				JFieldVar listener = holder.generatedClass.field(JMod.PRIVATE, listenerClass, "listener" + i + "_");
				listener.annotate(SuppressWarnings.class).param("value", "unused");
				listener.annotate(holder.classes().INJECT);
				i++;
			}
		}
	}

	private List<String> extractListenerClasses(Element activityElement) {

		List<? extends AnnotationMirror> annotationMirrors = activityElement.getAnnotationMirrors();

		String annotationName = RoboGuice.class.getName();
		AnnotationValue action = null;
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

	private void beforeCreateMethod(EBeanHolder holder, JFieldVar scope, JFieldVar eventManager, JMethod getInjector) {
		Classes classes = holder.classes();

		JBlock body = holder.initBody;
		JVar injector = body.decl(classes.INJECTOR, "injector_", invoke(getInjector));
		body.assign(scope, invoke(injector, "getInstance").arg(classes.CONTEXT_SCOPE.dotclass()));
		body.invoke(scope, "enter").arg(_this());
		body.invoke(injector, "injectMembers").arg(_this());
		body.assign(eventManager, invoke(injector, "getInstance").arg(classes.EVENT_MANAGER.dotclass()));
		fireEvent(holder, eventManager, body, holder.classes().ON_CREATE_EVENT, holder.beforeCreateSavedInstanceStateParam);
	}

}
