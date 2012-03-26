/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

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

import com.googlecode.androidannotations.annotations.RoboGuice;
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

public class RoboGuiceProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return RoboGuice.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getRelativeEBeanHolder(element);

		JClass injectorProviderClass = holder.refClass("roboguice.inject.InjectorProvider");
		holder.eBean._implements(injectorProviderClass);

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
		JClass injectorClass = holder.refClass("com.google.inject.Injector");
		JClass injectorProviderClass = holder.refClass("roboguice.inject.InjectorProvider");
		JMethod method = holder.eBean.method(JMod.PUBLIC, injectorClass, "getInjector");
		method.annotate(Override.class);
		JExpression castApplication = cast(injectorProviderClass, invoke("getApplication"));
		method.body()._return(castApplication.invoke("getInjector"));
		return method;
	}

	private void onRestartMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onRestart");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnRestartEvent");
	}

	private void onStartMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onStart");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnStartEvent");
	}

	private void onResumeMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onResume");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnResumeEvent");
	}

	private void onPauseMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onPause");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnPauseEvent");
	}

	private void onNewIntentMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onNewIntent");
		method.annotate(Override.class);
		JVar intent = method.param(holder.refClass("android.content.Intent"), "intent");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(intent);
		body.invoke(scope, "enter").arg(_this());
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnNewIntentEvent");
	}

	private void fireEvent(EBeanHolder holder, JFieldVar eventManager, JBlock body, String eventClassName, JExpression... eventArguments) {
		JClass eventClass = holder.refClass(eventClassName);
		JInvocation newEvent = _new(eventClass);
		for (JExpression eventArgument : eventArguments) {
			newEvent.arg(eventArgument);
		}
		body.invoke(eventManager, "fire").arg(newEvent);
	}

	private void onStopMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onStop");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = body._try();
		fireEvent(holder, eventManager, tryBlock.body(), "roboguice.activity.event.OnStopEvent");
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(_super(), method);
	}

	private void onDestroyMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onDestroy");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = body._try();
		fireEvent(holder, eventManager, tryBlock.body(), "roboguice.activity.event.OnDestroyEvent");
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(eventManager, "clear").arg(_this());
		finallyBody.invoke(scope, "exit").arg(_this());
		finallyBody.invoke(scope, "dispose").arg(_this());
		finallyBody.invoke(_super(), method);
	}

	private void onConfigurationChangedMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onConfigurationChanged");
		method.annotate(Override.class);
		JClass configurationClass = holder.refClass("android.content.res.Configuration");
		JVar newConfig = method.param(configurationClass, "newConfig");

		JBlock body = method.body();
		JVar currentConfig = body.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));

		body.invoke(_super(), method).arg(newConfig);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnConfigurationChangedEvent", currentConfig, newConfig);
	}

	private void onContentChangedMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onContentChanged");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnContentChangedEvent");
	}

	private void onActivityResultMethod(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "onActivityResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(codeModel.INT, "requestCode");
		JVar resultCode = method.param(codeModel.INT, "resultCode");
		JVar data = method.param(holder.refClass("android.content.Intent"), "data");

		JBlock body = method.body();

		body.invoke(_super(), method).arg(requestCode).arg(resultCode).arg(data);

		body.invoke(scope, "enter").arg(_this());

		JTryBlock tryBlock = body._try();
		fireEvent(holder, eventManager, tryBlock.body(), "roboguice.activity.event.OnActivityResultEvent", requestCode, resultCode, data);

		JBlock finallyBody = tryBlock._finally();
		finallyBody.invoke(scope, "exit").arg(_this());
	}

	private void afterSetContentView(JCodeModel codeModel, EBeanHolder holder, JFieldVar scope, JFieldVar eventManager) {
		JBlock afterSetContentViewBody = holder.afterSetContentView.body();
		afterSetContentViewBody.invoke(scope, "injectViews");
		fireEvent(holder, eventManager, afterSetContentViewBody, "roboguice.activity.event.OnContentViewAvailableEvent");
	}

	private JFieldVar eventManagerField(EBeanHolder holder) {
		JClass eventManagerClass = holder.refClass("roboguice.event.EventManager");
		JFieldVar eventManager = holder.eBean.field(JMod.PRIVATE, eventManagerClass, "eventManager_");
		return eventManager;
	}

	private JFieldVar scopeField(EBeanHolder holder) {
		JClass contextScopeClass = holder.refClass("roboguice.inject.ContextScope");
		JFieldVar scope = holder.eBean.field(JMod.PRIVATE, contextScopeClass, "scope_");
		return scope;
	}

	private void listenerFields(Element element, EBeanHolder holder) {
		JClass injectClass = holder.refClass("com.google.inject.Inject");
		List<String> listenerClasses = extractListenerClasses(element);
		if (listenerClasses.size() > 0) {
			int i = 1;
			for (String listenerClassName : listenerClasses) {
				JClass listenerClass = holder.refClass(listenerClassName);
				JFieldVar listener = holder.eBean.field(JMod.PRIVATE, listenerClass, "listener" + i + "_");
				listener.annotate(SuppressWarnings.class).param("value", "unused");
				listener.annotate(injectClass);
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
		JClass contextScopeClass = holder.refClass("roboguice.inject.ContextScope");
		JClass injectorClass = holder.refClass("com.google.inject.Injector");
		JClass eventManagerClass = holder.refClass("roboguice.event.EventManager");

		JBlock body = holder.init.body();
		JVar injector = body.decl(injectorClass, "injector_", invoke(getInjector));
		body.assign(scope, invoke(injector, "getInstance").arg(contextScopeClass.dotclass()));
		body.invoke(scope, "enter").arg(_this());
		body.invoke(injector, "injectMembers").arg(_this());
		body.assign(eventManager, invoke(injector, "getInstance").arg(eventManagerClass.dotclass()));
		fireEvent(holder, eventManager, body, "roboguice.activity.event.OnCreateEvent", holder.beforeCreateSavedInstanceStateParam);
	}

}
