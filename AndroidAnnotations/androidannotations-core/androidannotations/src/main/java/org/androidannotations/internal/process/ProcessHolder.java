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
package org.androidannotations.internal.process;

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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.GeneratedClassHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

public class ProcessHolder {

	// CHECKSTYLE:OFF
	public class Classes {

		/*
		 * Java
		 */
		public final AbstractJClass RUNTIME_EXCEPTION = refClass(RuntimeException.class);
		public final AbstractJClass EXCEPTION = refClass(Exception.class);
		public final AbstractJClass THROWABLE = refClass(Throwable.class);
		public final AbstractJClass CHAR_SEQUENCE = refClass(CharSequence.class);
		public final AbstractJClass CLASS_CAST_EXCEPTION = refClass(ClassCastException.class);
		public final AbstractJClass SERIALIZABLE = refClass(Serializable.class);
		public final AbstractJClass STRING = refClass(String.class);
		public final AbstractJClass STRING_BUILDER = refClass(StringBuilder.class);
		public final AbstractJClass SYSTEM = refClass(System.class);
		public final AbstractJClass INPUT_STREAM = refClass(InputStream.class);
		public final AbstractJClass FILE_INPUT_STREAM = refClass(FileInputStream.class);
		public final AbstractJClass SQL_EXCEPTION = refClass(SQLException.class);
		public final AbstractJClass COLLECTIONS = refClass(Collections.class);
		public final AbstractJClass THREAD = refClass(Thread.class);
		public final AbstractJClass HASH_MAP = refClass(HashMap.class);
		public final AbstractJClass LIST = refClass(List.class);
		public final AbstractJClass OBJECT = refClass(Object.class);
		public final AbstractJClass ARRAYS = refClass(Arrays.class);
		public final AbstractJClass HASH_SET = refClass(HashSet.class);

		/*
		 * Android
		 */
		public final AbstractJClass LOG = refClass(CanonicalNameConstants.LOG);
		public final AbstractJClass BUNDLE = refClass(CanonicalNameConstants.BUNDLE);
		public final AbstractJClass ACTIVITY = refClass(CanonicalNameConstants.ACTIVITY);
		public final AbstractJClass EDITABLE = refClass(CanonicalNameConstants.EDITABLE);
		public final AbstractJClass TEXT_WATCHER = refClass(CanonicalNameConstants.TEXT_WATCHER);
		public final AbstractJClass SEEKBAR = refClass(CanonicalNameConstants.SEEKBAR);
		public final AbstractJClass ON_SEEKBAR_CHANGE_LISTENER = refClass(CanonicalNameConstants.ON_SEEKBAR_CHANGE_LISTENER);
		public final AbstractJClass TEXT_VIEW = refClass(CanonicalNameConstants.TEXT_VIEW);
		public final AbstractJClass TEXT_VIEW_ON_EDITOR_ACTION_LISTENER = refClass(CanonicalNameConstants.TEXT_VIEW_ON_EDITOR_ACTION_LISTENER);
		public final AbstractJClass COMPOUND_BUTTON = refClass(CanonicalNameConstants.COMPOUND_BUTTON);
		public final AbstractJClass COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER = refClass(CanonicalNameConstants.COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER);
		public final AbstractJClass RADIO_GROUP = refClass(CanonicalNameConstants.RADIO_GROUP);
		public final AbstractJClass RADIO_GROUP_ON_CHECKED_CHANGE_LISTENER = refClass(CanonicalNameConstants.RADIO_GROUP_ON_CHECKED_CHANGE_LISTENER);
		public final AbstractJClass VIEW = refClass(CanonicalNameConstants.VIEW);
		public final AbstractJClass VIEW_ON_CLICK_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_CLICK_LISTENER);
		public final AbstractJClass VIEW_ON_TOUCH_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_TOUCH_LISTENER);
		public final AbstractJClass VIEW_ON_LONG_CLICK_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_LONG_CLICK_LISTENER);
		public final AbstractJClass VIEW_ON_FOCUS_CHANGE_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_FOCUS_CHANGE_LISTENER);
		public final AbstractJClass VIEW_GROUP_LAYOUT_PARAMS = refClass(CanonicalNameConstants.VIEW_GROUP_LAYOUT_PARAMS);
		public final AbstractJClass KEY_EVENT = refClass(CanonicalNameConstants.KEY_EVENT);
		public final AbstractJClass CONTEXT = refClass(CanonicalNameConstants.CONTEXT);
		public final AbstractJClass INTENT = refClass(CanonicalNameConstants.INTENT);
		public final AbstractJClass INTENT_FILTER = refClass(CanonicalNameConstants.INTENT_FILTER);
		public final AbstractJClass BROADCAST_RECEIVER = refClass(CanonicalNameConstants.BROADCAST_RECEIVER);
		public final AbstractJClass LOCAL_BROADCAST_MANAGER = refClass(CanonicalNameConstants.LOCAL_BROADCAST_MANAGER);
		public final AbstractJClass COMPONENT_NAME = refClass(CanonicalNameConstants.COMPONENT_NAME);
		public final AbstractJClass VIEW_GROUP = refClass(CanonicalNameConstants.VIEW_GROUP);
		public final AbstractJClass LAYOUT_INFLATER = refClass(CanonicalNameConstants.LAYOUT_INFLATER);
		public final AbstractJClass FRAGMENT_ACTIVITY = refClass(CanonicalNameConstants.FRAGMENT_ACTIVITY);
		public final AbstractJClass FRAGMENT = refClass(CanonicalNameConstants.FRAGMENT);
		public final AbstractJClass SUPPORT_V4_FRAGMENT = refClass(CanonicalNameConstants.SUPPORT_V4_FRAGMENT);
		public final AbstractJClass HTML = refClass(CanonicalNameConstants.HTML);
		public final AbstractJClass WINDOW_MANAGER_LAYOUT_PARAMS = refClass(CanonicalNameConstants.WINDOW_MANAGER_LAYOUT_PARAMS);
		public final AbstractJClass ADAPTER_VIEW = refClass(CanonicalNameConstants.ADAPTER_VIEW);
		public final AbstractJClass ON_ITEM_LONG_CLICK_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_LONG_CLICK_LISTENER);
		public final AbstractJClass ON_ITEM_CLICK_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_CLICK_LISTENER);
		public final AbstractJClass ON_ITEM_SELECTED_LISTENER = refClass(CanonicalNameConstants.ON_ITEM_SELECTED_LISTENER);
		public final AbstractJClass WINDOW = refClass(CanonicalNameConstants.WINDOW);
		public final AbstractJClass MENU_ITEM = refClass(CanonicalNameConstants.MENU_ITEM);
		public final AbstractJClass MENU_INFLATER = refClass(CanonicalNameConstants.MENU_INFLATER);
		public final AbstractJClass MENU = refClass(CanonicalNameConstants.MENU);
		public final AbstractJClass ANIMATION_UTILS = refClass(CanonicalNameConstants.ANIMATION_UTILS);
		public final AbstractJClass RESOURCES = refClass(CanonicalNameConstants.RESOURCES);
		public final AbstractJClass CONFIGURATION = refClass(CanonicalNameConstants.CONFIGURATION);
		public final AbstractJClass MOTION_EVENT = refClass(CanonicalNameConstants.MOTION_EVENT);
		public final AbstractJClass HANDLER = refClass(CanonicalNameConstants.HANDLER);
		public final AbstractJClass KEY_STORE = refClass(CanonicalNameConstants.KEY_STORE);
		public final AbstractJClass VIEW_SERVER = refClass(CanonicalNameConstants.VIEW_SERVER);
		public final AbstractJClass PARCELABLE = refClass(CanonicalNameConstants.PARCELABLE);
		public final AbstractJClass LOOPER = refClass(CanonicalNameConstants.LOOPER);
		public final AbstractJClass POWER_MANAGER = refClass(CanonicalNameConstants.POWER_MANAGER);
		public final AbstractJClass WAKE_LOCK = refClass(CanonicalNameConstants.WAKE_LOCK);
		public final AbstractJClass BUILD_VERSION = refClass(CanonicalNameConstants.BUILD_VERSION);
		public final AbstractJClass BUILD_VERSION_CODES = refClass(CanonicalNameConstants.BUILD_VERSION_CODES);
		public final AbstractJClass ACTIVITY_COMPAT = refClass(CanonicalNameConstants.ACTIVITY_COMPAT);
		public final AbstractJClass CONTEXT_COMPAT = refClass(CanonicalNameConstants.CONTEXT_COMPAT);
		public final AbstractJClass APP_WIDGET_MANAGER = refClass(CanonicalNameConstants.APP_WIDGET_MANAGER);
		public final AbstractJClass VIEW_PAGER = refClass(CanonicalNameConstants.VIEW_PAGER);
		public final AbstractJClass PAGE_CHANGE_LISTENER = refClass(CanonicalNameConstants.PAGE_CHANGE_LISTENER);

		public final AbstractJClass PREFERENCE = refClass(CanonicalNameConstants.PREFERENCE);
		public final AbstractJClass SUPPORT_V7_PREFERENCE = refClass(CanonicalNameConstants.SUPPORT_V7_PREFERENCE);
		public final AbstractJClass PREFERENCE_CHANGE_LISTENER = refClass(CanonicalNameConstants.PREFERENCE_CHANGE_LISTENER);
		public final AbstractJClass SUPPORT_V7_PREFERENCE_CHANGE_LISTENER = refClass(CanonicalNameConstants.SUPPORT_V7_PREFERENCE_CHANGE_LISTENER);
		public final AbstractJClass PREFERENCE_CLICK_LISTENER = refClass(CanonicalNameConstants.PREFERENCE_CLICK_LISTENER);
		public final AbstractJClass SUPPORT_V7_PREFERENCE_CLICK_LISTENER = refClass(CanonicalNameConstants.SUPPORT_V7_PREFERENCE_CLICK_LISTENER);
		public final AbstractJClass PREFERENCE_ACTIVITY_HEADER = refClass(CanonicalNameConstants.PREFERENCE_ACTIVITY_HEADER);

		/*
		 * HttpClient
		 */
		public final AbstractJClass CLIENT_CONNECTION_MANAGER = refClass(CanonicalNameConstants.CLIENT_CONNECTION_MANAGER);
		public final AbstractJClass DEFAULT_HTTP_CLIENT = refClass(CanonicalNameConstants.DEFAULT_HTTP_CLIENT);
		public final AbstractJClass SSL_SOCKET_FACTORY = refClass(CanonicalNameConstants.SSL_SOCKET_FACTORY);
		public final AbstractJClass PLAIN_SOCKET_FACTORY = refClass(CanonicalNameConstants.PLAIN_SOCKET_FACTORY);
		public final AbstractJClass SCHEME = refClass(CanonicalNameConstants.SCHEME);
		public final AbstractJClass SCHEME_REGISTRY = refClass(CanonicalNameConstants.SCHEME_REGISTRY);
		public final AbstractJClass SINGLE_CLIENT_CONN_MANAGER = refClass(CanonicalNameConstants.SINGLE_CLIENT_CONN_MANAGER);
	}

	// CHECKSTYLE:ON

	private final Map<Element, GeneratedClassHolder> generatedClassHolders = new HashMap<>();

	private final ProcessingEnvironment processingEnvironment;

	private final JCodeModel codeModel;

	private final Map<String, AbstractJClass> loadedClasses = new HashMap<>();

	private final Classes classes;

	private final OriginatingElements originatingElements = new OriginatingElements();

	public ProcessHolder(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		codeModel = new JCodeModel();
		classes = new Classes();
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

	public AbstractJClass refClass(Class<?> clazz) {
		AbstractJClass referencedClass = codeModel.ref(clazz);
		loadedClasses.put(clazz.getCanonicalName(), referencedClass);
		return referencedClass;
	}

	public AbstractJClass refClass(String fullyQualifiedClassName) {

		int arrayCounter = 0;
		while (fullyQualifiedClassName.endsWith("[]")) {
			arrayCounter++;
			fullyQualifiedClassName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.length() - 2);
		}

		AbstractJClass refClass = loadedClasses.get(fullyQualifiedClassName);

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

}
