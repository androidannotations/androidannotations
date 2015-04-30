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
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.invoke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.holder.RoboGuiceHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
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

		holder.getGeneratedClass()._implements(classes().ROBO_CONTEXT);

		JFieldVar scope = roboGuiceHolder.getScopeField();
		JFieldVar scopedObjects = roboGuiceHolder.getScopedObjectsField();
		JFieldVar eventManager = roboGuiceHolder.getEventManagerField();
		roboGuiceHolder.getContentViewListenerField();
		listenerFields(element, holder);

		beforeCreateMethod(holder, scope, scopedObjects, eventManager);
		onRestartMethod(roboGuiceHolder, eventManager);
		onStartMethod(roboGuiceHolder, eventManager);
		onResumeMethod(roboGuiceHolder, eventManager);
		onPauseMethod(roboGuiceHolder, eventManager);
		onNewIntentMethod(roboGuiceHolder, eventManager);
		onStopMethod(roboGuiceHolder, eventManager);
		onDestroyMethod(roboGuiceHolder, eventManager);
		onConfigurationChangedMethod(roboGuiceHolder, eventManager);
		onContentChangedMethod(roboGuiceHolder, holder, scope, eventManager);
		onActivityResultMethod(roboGuiceHolder, eventManager);
		getScopedObjectMap(holder, scopedObjects);
	}

	private void listenerFields(Element element, EActivityHolder holder) {
		List<TypeMirror> listenerTypeMirrors = extractListenerTypeMirrors(element);
		int i = 1;
		for (TypeMirror listenterTypeMirror : listenerTypeMirrors) {
			JClass listenerClass = codeModelHelper.typeMirrorToJClass(listenterTypeMirror, holder);
			JFieldVar listener = holder.getGeneratedClass().field(JMod.PRIVATE, listenerClass, "listener" + i + "_");
			codeModelHelper.addSuppressWarnings(listener, "unused");
			listener.annotate(classes().INJECT);
			i++;
		}
	}

	private List<TypeMirror> extractListenerTypeMirrors(Element activityElement) {

		List<? extends AnnotationMirror> annotationMirrors = activityElement.getAnnotationMirrors();

		String annotationName = RoboGuice.class.getName();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationName.equals(annotationMirror.getAnnotationType().toString())) {
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					if ("value".equals(entry.getKey().getSimpleName().toString())) {
						AnnotationValue action = entry.getValue();
						@SuppressWarnings("unchecked")
						List<AnnotationValue> elements = (List<AnnotationValue>) action.getValue();
						List<TypeMirror> listenerTypeMirrors = new ArrayList<TypeMirror>(elements.size());

						for (AnnotationValue annotationValue : elements) {
							listenerTypeMirrors.add((TypeMirror) annotationValue.getValue());
						}

						return listenerTypeMirrors;
					}
				}
			}
		}
		return Collections.emptyList();
	}

	private void beforeCreateMethod(EActivityHolder holder, JFieldVar scope, JFieldVar scopedObjects, JFieldVar eventManager) {
		JBlock body = holder.getInitBody();
		JClass keyWildCard = classes().KEY.narrow(codeModel().wildcard());
		JClass scopedHashMap = classes().HASH_MAP.narrow(keyWildCard, classes().OBJECT);
		body.assign(scopedObjects, JExpr._new(scopedHashMap));

		JVar injector = body.decl(classes().ROBO_INJECTOR, "injector_", classes().ROBO_GUICE.staticInvoke("getInjector").arg(_this()));
		body.assign(scope, invoke(injector, "getInstance").arg(classes().CONTEXT_SCOPE.dotclass()));
		body.assign(eventManager, invoke(injector, "getInstance").arg(classes().EVENT_MANAGER.dotclass()));
		body.add(injector.invoke("injectMembersWithoutViews").arg(_this()));
		fireEvent(eventManager, body, classes().ON_CREATE_EVENT, holder.getInitSavedInstanceParam());
	}

	private void onRestartMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onRestartAfterSuperBlock = holder.getOnRestartAfterSuperBlock();
		fireEvent(eventManager, onRestartAfterSuperBlock, classes().ON_RESTART_EVENT);
	}

	private void onStartMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onStartAfterSuperBlock = holder.getOnStartAfterSuperBlock();
		fireEvent(eventManager, onStartAfterSuperBlock, classes().ON_START_EVENT);
	}

	private void onResumeMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onResumeAfterSuperBlock = holder.getOnResumeAfterSuperBlock();
		fireEvent(eventManager, onResumeAfterSuperBlock, classes().ON_RESUME_EVENT);
	}

	private void onPauseMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onPauseAfterSuperBlock = holder.getOnPauseAfterSuperBlock();
		fireEvent(eventManager, onPauseAfterSuperBlock, classes().ON_PAUSE_EVENT);
	}

	private void onNewIntentMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onNewIntentAfterSuperBlock = holder.getOnNewIntentAfterSuperBlock();
		fireEvent(eventManager, onNewIntentAfterSuperBlock, classes().ON_NEW_INTENT_EVENT);
	}

	private void onStopMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onStopBlock = new JBlock(false, false);

		JTryBlock tryBlock = onStopBlock._try();
		fireEvent(eventManager, tryBlock.body(), classes().ON_STOP_EVENT);
		JBlock finallyBody = tryBlock._finally();

		finallyBody.invoke(_super(), "onStop");

		JMethod onStop = holder.getOnStop();
		codeModelHelper.replaceSuperCall(onStop, onStopBlock);
	}

	private void onDestroyMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onDestroyBlock = new JBlock(false, false);

		JTryBlock tryBlock = onDestroyBlock._try();
		fireEvent(eventManager, tryBlock.body(), classes().ON_DESTROY_EVENT);
		JBlock finallyBody = tryBlock._finally();

		JTryBlock tryInFinally = finallyBody._try();
		tryInFinally.body().add(classes().ROBO_GUICE.staticInvoke("destroyInjector").arg(_this()));
		tryInFinally._finally().invoke(_super(), "onDestroy");

		JMethod onDestroy = holder.getOnDestroy();
		codeModelHelper.replaceSuperCall(onDestroy, onDestroyBlock);
	}

	private void onConfigurationChangedMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JVar currentConfig = holder.getCurrentConfig();
		JBlock onConfigurationChangedAfterSuperBlock = holder.getOnConfigurationChangedAfterSuperBlock();
		JExpression newConfig = holder.getNewConfig();
		fireEvent(eventManager, onConfigurationChangedAfterSuperBlock, classes().ON_CONFIGURATION_CHANGED_EVENT, currentConfig, newConfig);
	}

	private void onContentChangedMethod(RoboGuiceHolder holder, EActivityHolder eActivityHolder, JFieldVar scope, JFieldVar eventManager) {
		JBlock onContentChangedAfterSuperBlock = holder.getOnContentChangedAfterSuperBlock();

		// no synchronized in CodeModel
		// https://java.net/jira/browse/CODEMODEL-6
		onContentChangedAfterSuperBlock.directStatement("synchronized(" + classes().CONTEXT_SCOPE.name() + ".class" + ")");
		JBlock synchronizedBlock = new JBlock(true, true);
		synchronizedBlock.invoke(scope, "enter").arg(_this());
		JTryBlock tryBlock = synchronizedBlock._try();
		tryBlock.body().staticInvoke(eActivityHolder.refClass(org.androidannotations.api.roboguice.RoboGuiceHelper.class), "callInjectViews").arg(_this());
		tryBlock._finally().invoke(scope, "exit").arg(_this());
		onContentChangedAfterSuperBlock.add(synchronizedBlock);

		fireEvent(eventManager, onContentChangedAfterSuperBlock, classes().ON_CONTENT_CHANGED_EVENT);
	}

	private void onActivityResultMethod(RoboGuiceHolder holder, JFieldVar eventManager) {
		JBlock onActivityResultAfterSuperBlock = holder.getOnActivityResultAfterSuperBlock();
		JVar requestCode = holder.getRequestCode();
		JVar resultCode = holder.getResultCode();
		JVar data = holder.getData();

		fireEvent(eventManager, onActivityResultAfterSuperBlock, classes().ON_ACTIVITY_RESULT_EVENT, requestCode, resultCode, data);
	}

	private void fireEvent(JFieldVar eventManager, JBlock body, JClass eventClass, JExpression... eventArguments) {
		JClass actualEventClass = eventClass;
		if (eventClass.fullName().startsWith("roboguice.context.event")) {
			actualEventClass = eventClass.narrow(classes().ACTIVITY);
		}

		JInvocation newEvent = _new(actualEventClass);
		newEvent.arg(_this());
		for (JExpression eventArgument : eventArguments) {
			newEvent.arg(eventArgument);
		}
		body.invoke(eventManager, "fire").arg(newEvent);
	}

	private void getScopedObjectMap(EActivityHolder holder, JFieldVar scopedObjectMap) {
		JMethod getScopedObjectMapMethod = holder.getGeneratedClass().method(JMod.PUBLIC, scopedObjectMap.type(), "getScopedObjectMap");
		getScopedObjectMapMethod.annotate(Override.class);
		getScopedObjectMapMethod.body()._return(scopedObjectMap);
	}
}
