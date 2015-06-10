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
package org.androidannotations.process;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.GeneratedClassHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class ProcessHolder {

	// CHECKSTYLE:OFF
	public class Classes {

		/*
		 * Java
		 */
		public final JClass RUNTIME_EXCEPTION = refClass(RuntimeException.class);
		public final JClass EXCEPTION = refClass(Exception.class);
		public final JClass THROWABLE = refClass(Throwable.class);
		public final JClass CHAR_SEQUENCE = refClass(CharSequence.class);
		public final JClass CLASS_CAST_EXCEPTION = refClass(ClassCastException.class);
		public final JClass SERIALIZABLE = refClass(Serializable.class);
		public final JClass STRING = refClass(String.class);
		public final JClass STRING_BUILDER = refClass(StringBuilder.class);
		public final JClass SYSTEM = refClass(System.class);
		public final JClass INPUT_STREAM = refClass(InputStream.class);
		public final JClass FILE_INPUT_STREAM = refClass(FileInputStream.class);
		public final JClass SQL_EXCEPTION = refClass(SQLException.class);
		public final JClass COLLECTIONS = refClass(Collections.class);
		public final JClass THREAD = refClass(Thread.class);
		public final JClass HASH_MAP = refClass(HashMap.class);
		public final JClass LIST = refClass(List.class);
		public final JClass OBJECT = refClass(Object.class);
		public final JClass ARRAYS = refClass(Arrays.class);
		public final JClass HASH_SET = refClass(HashSet.class);

		/*
		 * Android
		 */
		public final JClass LOG = refClass(CanonicalNameConstants.LOG);
		public final JClass BUNDLE = refClass(CanonicalNameConstants.BUNDLE);
		public final JClass ACTIVITY = refClass(CanonicalNameConstants.ACTIVITY);
		public final JClass EDITABLE = refClass(CanonicalNameConstants.EDITABLE);
		public final JClass TEXT_WATCHER = refClass(CanonicalNameConstants.TEXT_WATCHER);
		public final JClass SEEKBAR = refClass(CanonicalNameConstants.SEEKBAR);
		public final JClass ON_SEEKBAR_CHANGE_LISTENER = refClass(CanonicalNameConstants.ON_SEEKBAR_CHANGE_LISTENER);
		public final JClass TEXT_VIEW = refClass(CanonicalNameConstants.TEXT_VIEW);
		public final JClass TEXT_VIEW_ON_EDITOR_ACTION_LISTENER = refClass(CanonicalNameConstants.TEXT_VIEW_ON_EDITOR_ACTION_LISTENER);
		public final JClass COMPOUND_BUTTON = refClass(CanonicalNameConstants.COMPOUND_BUTTON);
		public final JClass COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER = refClass(CanonicalNameConstants.COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER);
		public final JClass VIEW = refClass(CanonicalNameConstants.VIEW);
		public final JClass VIEW_ON_CLICK_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_CLICK_LISTENER);
		public final JClass VIEW_ON_TOUCH_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_TOUCH_LISTENER);
		public final JClass VIEW_ON_LONG_CLICK_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_LONG_CLICK_LISTENER);
		public final JClass VIEW_ON_FOCUS_CHANGE_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_FOCUS_CHANGE_LISTENER);
		public final JClass VIEW_GROUP_LAYOUT_PARAMS = refClass(CanonicalNameConstants.VIEW_GROUP_LAYOUT_PARAMS);
		public final JClass KEY_EVENT = refClass(CanonicalNameConstants.KEY_EVENT);
		public final JClass CONTEXT = refClass(CanonicalNameConstants.CONTEXT);
		public final JClass INTENT = refClass(CanonicalNameConstants.INTENT);
		public final JClass INTENT_FILTER = refClass(CanonicalNameConstants.INTENT_FILTER);
		public final JClass BROADCAST_RECEIVER = refClass(CanonicalNameConstants.BROADCAST_RECEIVER);
		public final JClass LOCAL_BROADCAST_MANAGER = refClass(CanonicalNameConstants.LOCAL_BROADCAST_MANAGER);
		public final JClass COMPONENT_NAME = refClass(CanonicalNameConstants.COMPONENT_NAME);
		public final JClass VIEW_GROUP = refClass(CanonicalNameConstants.VIEW_GROUP);
		public final JClass LAYOUT_INFLATER = refClass(CanonicalNameConstants.LAYOUT_INFLATER);
		public final JClass FRAGMENT_ACTIVITY = refClass(CanonicalNameConstants.FRAGMENT_ACTIVITY);
		public final JClass FRAGMENT = refClass(CanonicalNameConstants.FRAGMENT);
		public final JClass SUPPORT_V4_FRAGMENT = refClass(CanonicalNameConstants.SUPPORT_V4_FRAGMENT);
		public final JClass HTML = refClass(CanonicalNameConstants.HTML);
		public final JClass WINDOW_MANAGER_LAYOUT_PARAMS = refClass(CanonicalNameConstants.WINDOW_MANAGER_LAYOUT_PARAMS);
		public final JClass ADAPTER_VIEW = refClass(CanonicalNameConstants.ADAPTER_VIEW);
		public final JClass ON_ITEM_LONG_CLICK_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_LONG_CLICK_LISTENER);
		public final JClass ON_ITEM_CLICK_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_CLICK_LISTENER);
		public final JClass ON_ITEM_SELECTED_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_SELECTED_LISTENER);
		public final JClass WINDOW = refClass(CanonicalNameConstants.WINDOW);
		public final JClass MENU_ITEM = refClass(CanonicalNameConstants.MENU_ITEM);
		public final JClass MENU_INFLATER = refClass(CanonicalNameConstants.MENU_INFLATER);
		public final JClass MENU = refClass(CanonicalNameConstants.MENU);
		public final JClass ANIMATION_UTILS = refClass(CanonicalNameConstants.ANIMATION_UTILS);
		public final JClass RESOURCES = refClass(CanonicalNameConstants.RESOURCES);
		public final JClass CONFIGURATION = refClass(CanonicalNameConstants.CONFIGURATION);
		public final JClass MOTION_EVENT = refClass(CanonicalNameConstants.MOTION_EVENT);
		public final JClass HANDLER = refClass(CanonicalNameConstants.HANDLER);
		public final JClass KEY_STORE = refClass(CanonicalNameConstants.KEY_STORE);
		public final JClass VIEW_SERVER = refClass(CanonicalNameConstants.VIEW_SERVER);
		public final JClass PARCELABLE = refClass(CanonicalNameConstants.PARCELABLE);
		public final JClass LOOPER = refClass(CanonicalNameConstants.LOOPER);
		public final JClass POWER_MANAGER = refClass(CanonicalNameConstants.POWER_MANAGER);
		public final JClass WAKE_LOCK = refClass(CanonicalNameConstants.WAKE_LOCK);
		public final JClass BUILD_VERSION = refClass(CanonicalNameConstants.BUILD_VERSION);
		public final JClass BUILD_VERSION_CODES = refClass(CanonicalNameConstants.BUILD_VERSION_CODES);
		public final JClass ACTIVITY_COMPAT = refClass(CanonicalNameConstants.ACTIVITY_COMPAT);
		public final JClass CONTEXT_COMPAT = refClass(CanonicalNameConstants.CONTEXT_COMPAT);
		public final JClass APP_WIDGET_MANAGER = refClass(CanonicalNameConstants.APP_WIDGET_MANAGER);

		public final JClass PREFERENCE = refClass(CanonicalNameConstants.PREFERENCE);
		public final JClass PREFERENCE_CHANGE_LISTENER = refClass(CanonicalNameConstants.PREFERENCE_CHANGE_LISTENER);
		public final JClass PREFERENCE_CLICK_LISTENER = refClass(CanonicalNameConstants.PREFERENCE_CLICK_LISTENER);
		public final JClass PREFERENCE_ACTIVITY_HEADER = refClass(CanonicalNameConstants.PREFERENCE_ACTIVITY_HEADER);

		/*
		 * RoboGuice
		 */
		public final JClass ROBO_CONTEXT = refClass(CanonicalNameConstants.ROBO_CONTEXT);
		public final JClass ROBO_INJECTOR = refClass(CanonicalNameConstants.ROBO_INJECTOR);
		public final JClass CONTENT_VIEW_LISTENER = refClass(CanonicalNameConstants.CONTENT_VIEW_LISTENER);
		public final JClass KEY = refClass(CanonicalNameConstants.KEY);
		public final JClass ON_RESTART_EVENT = refClass(CanonicalNameConstants.ON_RESTART_EVENT);
		public final JClass ON_START_EVENT = refClass(CanonicalNameConstants.ON_START_EVENT);
		public final JClass ON_RESUME_EVENT = refClass(CanonicalNameConstants.ON_RESUME_EVENT);
		public final JClass ON_PAUSE_EVENT = refClass(CanonicalNameConstants.ON_PAUSE_EVENT);
		public final JClass ON_NEW_INTENT_EVENT = refClass(CanonicalNameConstants.ON_NEW_INTENT_EVENT);
		public final JClass EVENT_MANAGER = refClass(CanonicalNameConstants.EVENT_MANAGER);
		public final JClass CONTEXT_SCOPE = refClass(CanonicalNameConstants.CONTEXT_SCOPE);
		public final JClass VIEW_MEMBERS_INJECTOR = refClass(CanonicalNameConstants.VIEW_MEMBERS_INJECTOR);
		public final JClass ROBO_GUICE = refClass(CanonicalNameConstants.ROBO_GUICE);
		public final JClass INJECT = refClass(CanonicalNameConstants.INJECT);
		public final JClass ON_STOP_EVENT = refClass(CanonicalNameConstants.ON_STOP_EVENT);
		public final JClass ON_DESTROY_EVENT = refClass(CanonicalNameConstants.ON_DESTROY_EVENT);
		public final JClass ON_CONFIGURATION_CHANGED_EVENT = refClass(CanonicalNameConstants.ON_CONFIGURATION_CHANGED_EVENT);
		public final JClass ON_CONTENT_CHANGED_EVENT = refClass(CanonicalNameConstants.ON_CONTENT_CHANGED_EVENT);
		public final JClass ON_ACTIVITY_RESULT_EVENT = refClass(CanonicalNameConstants.ON_ACTIVITY_RESULT_EVENT);
		public final JClass ON_CREATE_EVENT = refClass(CanonicalNameConstants.ON_CREATE_EVENT);

		/*
		 * OrmLite
		 */
		public final JClass CONNECTION_SOURCE = refClass(CanonicalNameConstants.CONNECTION_SOURCE);
		public final JClass OPEN_HELPER_MANAGER = refClass(CanonicalNameConstants.OPEN_HELPER_MANAGER);
		public final JClass RUNTIME_EXCEPTION_DAO = refClass(CanonicalNameConstants.RUNTIME_EXCEPTION_DAO);
		public final JClass DAO_MANAGER = refClass(CanonicalNameConstants.DAO_MANAGER);

		/*
		 * HttpClient
		 */
		public final JClass CLIENT_CONNECTION_MANAGER = refClass(CanonicalNameConstants.CLIENT_CONNECTION_MANAGER);
		public final JClass DEFAULT_HTTP_CLIENT = refClass(CanonicalNameConstants.DEFAULT_HTTP_CLIENT);
		public final JClass SSL_SOCKET_FACTORY = refClass(CanonicalNameConstants.SSL_SOCKET_FACTORY);
		public final JClass PLAIN_SOCKET_FACTORY = refClass(CanonicalNameConstants.PLAIN_SOCKET_FACTORY);
		public final JClass SCHEME = refClass(CanonicalNameConstants.SCHEME);
		public final JClass SCHEME_REGISTRY = refClass(CanonicalNameConstants.SCHEME_REGISTRY);
		public final JClass SINGLE_CLIENT_CONN_MANAGER = refClass(CanonicalNameConstants.SINGLE_CLIENT_CONN_MANAGER);

		/*
		 * SpringFramework
		 */
		public final JClass REST_TEMPLATE = refClass(CanonicalNameConstants.REST_TEMPLATE);
		public final JClass HTTP_METHOD = refClass(CanonicalNameConstants.HTTP_METHOD);
		public final JClass HTTP_ENTITY = refClass(CanonicalNameConstants.HTTP_ENTITY);
		public final JClass HTTP_HEADERS = refClass(CanonicalNameConstants.HTTP_HEADERS);
		public final JClass MEDIA_TYPE = refClass(CanonicalNameConstants.MEDIA_TYPE);
		public final JClass RESPONSE_ENTITY = refClass(CanonicalNameConstants.RESPONSE_ENTITY);
		public final JClass HTTP_AUTHENTICATION = refClass(CanonicalNameConstants.HTTP_AUTHENTICATION);
		public final JClass HTTP_BASIC_AUTHENTICATION = refClass(CanonicalNameConstants.HTTP_BASIC_AUTHENTICATION);
		public final JClass REST_CLIENT_EXCEPTION = refClass(CanonicalNameConstants.REST_CLIENT_EXCEPTION);
		public final JClass NESTED_RUNTIME_EXCEPTION = refClass(CanonicalNameConstants.NESTED_RUNTIME_EXCEPTION);
	}

	// CHECKSTYLE:ON

	private final Map<Element, GeneratedClassHolder> generatedClassHolders = new HashMap<>();

	private final ProcessingEnvironment processingEnvironment;

	private final JCodeModel codeModel;

	private final Map<String, JClass> loadedClasses = new HashMap<>();

	private final Classes classes;

	private final Set<Class<?>> apiClassesToGenerate = new HashSet<>();

	private final OriginatingElements originatingElements = new OriginatingElements();

	public ProcessHolder(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		codeModel = new JCodeModel();
		classes = new Classes();
		refClass(CanonicalNameConstants.STRING);
		preloadJavaLangClasses();
	}

	private void preloadJavaLangClasses() {
		loadedClasses.put(String.class.getName(), refClass(String.class));
		loadedClasses.put(Object.class.getName(), refClass(Object.class));
	}

	public void put(Element element, GeneratedClassHolder generatedClassHolder) {

		JDefinedClass generatedClass = generatedClassHolder.getGeneratedClass();

		String qualifiedName = generatedClass.fullName();

		originatingElements.add(qualifiedName, element);

		generatedClassHolders.put(element, generatedClassHolder);
	}

	public GeneratedClassHolder getGeneratedClassHolder(Element element) {
		for (Element key : generatedClassHolders.keySet()) {
			if (key.asType().toString().equals(element.asType().toString())) {
				return generatedClassHolders.get(key);
			}
		}
		return null;
	}

	public JClass refClass(Class<?> clazz) {
		return codeModel.ref(clazz);
	}

	public JClass refClass(String fullyQualifiedClassName) {

		int arrayCounter = 0;
		while (fullyQualifiedClassName.endsWith("[]")) {
			arrayCounter++;
			fullyQualifiedClassName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.length() - 2);
		}

		JClass refClass = loadedClasses.get(fullyQualifiedClassName);

		if (refClass == null) {
			refClass = codeModel.directClass(fullyQualifiedClassName);
			loadedClasses.put(fullyQualifiedClassName, refClass);
		}

		for (int i = 0; i < arrayCounter; i++) {
			refClass = refClass.array();
		}

		return refClass;
	}

	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		JDefinedClass refClass = (JDefinedClass) loadedClasses.get(fullyQualifiedClassName);
		if (refClass == null) {
			try {
				refClass = codeModel._class(fullyQualifiedClassName);
			} catch (JClassAlreadyExistsException e) {
				refClass = (JDefinedClass) refClass(fullyQualifiedClassName);
			}
			loadedClasses.put(fullyQualifiedClassName, refClass);
		}
		return refClass;
	}

	public ProcessingEnvironment processingEnvironment() {
		return processingEnvironment;
	}

	public JCodeModel codeModel() {
		return codeModel;
	}

	public Classes classes() {
		return classes;
	}

	public OriginatingElements getOriginatingElements() {
		return originatingElements;
	}

	public Set<Class<?>> getApiClassesToGenerate() {
		return apiClassesToGenerate;
	}

	public void generateApiClass(Element originatingElement, Class<?> apiClass) {
		originatingElements.add(apiClass.getCanonicalName(), originatingElement);
		apiClassesToGenerate.add(apiClass);
	}
}
