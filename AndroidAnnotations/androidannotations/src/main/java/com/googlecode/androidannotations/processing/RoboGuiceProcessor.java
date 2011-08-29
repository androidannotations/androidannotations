/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class RoboGuiceProcessor implements ElementProcessor {

    @Override
    public Class<? extends Annotation> getTarget() {
        return RoboGuice.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
        ActivityHolder holder = activitiesHolder.getRelativeActivityHolder(element);

        JClass injectorProviderClass = holder.refClass("roboguice.inject.InjectorProvider");
        holder.activity._implements(injectorProviderClass);

        // Fields
        JFieldVar scope = scopeField(holder);
        JFieldVar eventManager = eventManagerField(holder);
        listenerFields(element, holder);

        // Methods
        setContentViewMethods(codeModel, holder, scope, eventManager);
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

    private JMethod getInjectorMethod(ActivityHolder holder) {
        JClass injectorClass = holder.refClass("com.google.inject.Injector");
        JClass injectorProviderClass = holder.refClass("roboguice.inject.InjectorProvider");
        JMethod method = holder.activity.method(JMod.PUBLIC, injectorClass, "getInjector");
        method.annotate(Override.class);
        JExpression castApplication = cast(injectorProviderClass, invoke("getApplication"));
        method.body()._return(castApplication.invoke("getInjector"));
        return method;
    }

    private void onRestartMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onRestart");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(scope, "enter").arg(_this());
        body.invoke(_super(), method);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnRestartEvent");
    }

    private void onStartMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onStart");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(scope, "enter").arg(_this());
        body.invoke(_super(), method);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnStartEvent");
    }

    private void onResumeMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onResume");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(scope, "enter").arg(_this());
        body.invoke(_super(), method);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnResumeEvent");
    }

    private void onPauseMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onPause");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(_super(), method);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnPauseEvent");
    }

    private void onNewIntentMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onNewIntent");
        method.annotate(Override.class);
        JVar intent = method.param(holder.refClass("android.content.Intent"), "intent");
        JBlock body = method.body();
        body.invoke(_super(), method).arg(intent);
        body.invoke(scope, "enter").arg(_this());
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnNewIntentEvent");
    }

    private void fireEvent(ActivityHolder holder, JFieldVar eventManager, JBlock body, String eventClassName, JExpression... eventArguments) {
        JClass eventClass = holder.refClass(eventClassName);
        JInvocation newEvent = _new(eventClass);
        for (JExpression eventArgument : eventArguments) {
            newEvent.arg(eventArgument);
        }
        body.invoke(eventManager, "fire").arg(newEvent);
    }
    
    private void onStopMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onStop");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(scope, "enter").arg(_this());
        
        JTryBlock tryBlock = body._try();
        fireEvent(holder, eventManager, tryBlock.body(), "roboguice.activity.event.OnStopEvent");
        JBlock finallyBody = tryBlock._finally();
        
        finallyBody.invoke(scope, "exit").arg(_this());
        finallyBody.invoke(_super(), method);
    }
    
    private void onDestroyMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onDestroy");
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
    
    private void onConfigurationChangedMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PUBLIC, codeModel.VOID, "onConfigurationChanged");
        method.annotate(Override.class);
        JClass configurationClass = holder.refClass("android.content.res.Configuration");
		JVar newConfig = method.param(configurationClass, "newConfig");
        
        JBlock body = method.body();
        JVar currentConfig = body.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));
        
        body.invoke(_super(), method).arg(newConfig);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnConfigurationChangedEvent", currentConfig, newConfig);
    }
    
    private void onContentChangedMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PUBLIC, codeModel.VOID, "onContentChanged");
        method.annotate(Override.class);
        JBlock body = method.body();
        body.invoke(_super(), method);
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnContentChangedEvent");
    }
    
    private void onActivityResultMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {
        JMethod method = holder.activity.method(JMod.PROTECTED, codeModel.VOID, "onActivityResult");
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

    private void setContentViewMethods(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager) {

        JClass viewClass = holder.refClass("android.view.View");
        JClass layoutParamsClass = holder.refClass("android.view.ViewGroup.LayoutParams");

        setContentViewMethod(codeModel, holder, scope, eventManager, new JType[] { codeModel.INT }, new String[] { "layoutResID" });
        setContentViewMethod(codeModel, holder, scope, eventManager, new JType[] { viewClass, layoutParamsClass }, new String[] { "view", "params" });
        setContentViewMethod(codeModel, holder, scope, eventManager, new JType[] { viewClass }, new String[] { "view" });
    }

    private void setContentViewMethod(JCodeModel codeModel, ActivityHolder holder, JFieldVar scope, JFieldVar eventManager, JType[] paramTypes, String[] paramNames) {
        JMethod method = holder.activity.method(JMod.PUBLIC, codeModel.VOID, "setContentView");

        method.annotate(Override.class);

        ArrayList<JVar> params = new ArrayList<JVar>();
        for (int i = 0; i < paramTypes.length; i++) {
            JVar param = method.param(paramTypes[i], paramNames[i]);
            params.add(param);
        }
        JBlock body = method.body();
        JInvocation superCall = body.invoke(_super(), method);
        for (JVar arg : params) {
            superCall.arg(arg);
        }
        body.invoke(scope, "injectViews");
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnContentViewAvailableEvent");
    }

    private JFieldVar eventManagerField(ActivityHolder holder) {
        JClass eventManagerClass = holder.refClass("roboguice.event.EventManager");
        JFieldVar eventManager = holder.activity.field(JMod.PRIVATE, eventManagerClass, "eventManager_");
        return eventManager;
    }

    private JFieldVar scopeField(ActivityHolder holder) {
        JClass contextScopeClass = holder.refClass("roboguice.inject.ContextScope");
        JFieldVar scope = holder.activity.field(JMod.PRIVATE, contextScopeClass, "scope_");
        return scope;
    }

    private void listenerFields(Element element, ActivityHolder holder) {
        JClass injectClass = holder.refClass("com.google.inject.Inject");
        List<String> listenerClasses = extractListenerClasses(element);
        if (listenerClasses.size() > 0) {
            int i = 1;
            for (String listenerClassName : listenerClasses) {
                JClass listenerClass = holder.refClass(listenerClassName);
                JFieldVar listener = holder.activity.field(JMod.PRIVATE, listenerClass, "listener" + i + "_");
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

    private void beforeCreateMethod(ActivityHolder holder, JFieldVar scope, JFieldVar eventManager, JMethod getInjector) {
        JClass contextScopeClass = holder.refClass("roboguice.inject.ContextScope");
        JClass injectorClass = holder.refClass("com.google.inject.Injector");
        JClass eventManagerClass = holder.refClass("roboguice.event.EventManager");

        JBlock body = holder.beforeCreate.body();
        JVar injector = body.decl(injectorClass, "injector_", invoke(getInjector));
        body.assign(scope, invoke(injector, "getInstance").arg(contextScopeClass.dotclass()));
        body.invoke(scope, "enter").arg(_this());
        body.invoke(injector, "injectMembers").arg(_this());
        body.assign(eventManager, invoke(injector, "getInstance").arg(eventManagerClass.dotclass()));
        fireEvent(holder, eventManager, body, "roboguice.activity.event.OnCreateEvent", holder.beforeCreateSavedInstanceStateParam);
    }

}
