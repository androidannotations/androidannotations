/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public class EBeansHolder {

	public class Classes {

		/*
		 * Java
		 */
		public final JClass RUNTIME_EXCEPTION = refClass(RuntimeException.class);
		public final JClass EXCEPTION = refClass(Exception.class);
		public final JClass CHAR_SEQUENCE = refClass(CharSequence.class);
		public final JClass CLASS_CAST_EXCEPTION = refClass(ClassCastException.class);
		public final JClass SERIALIZABLE = refClass(Serializable.class);
		public final JClass STRING = refClass(String.class);
		public final JClass SYSTEM = refClass(System.class);
		public final JClass INPUT_STREAM = refClass(InputStream.class);
		public final JClass FILE_INPUT_STREAM = refClass(FileInputStream.class);
		public final JClass SQL_EXCEPTION = refClass(SQLException.class);

		/*
		 * Android
		 */
		public final JClass LOG = refClass(CanonicalNameConstants.LOG);
		public final JClass BUNDLE = refClass(CanonicalNameConstants.BUNDLE);
		public final JClass ACTIVITY = refClass(CanonicalNameConstants.ACTIVITY);
		public final JClass EDITABLE = refClass(CanonicalNameConstants.EDITABLE);
		public final JClass TEXT_WATCHER = refClass(CanonicalNameConstants.TEXT_WATCHER);
		public final JClass TEXT_VIEW = refClass(CanonicalNameConstants.TEXT_VIEW);
		public final JClass VIEW = refClass(CanonicalNameConstants.VIEW);
		public final JClass VIEW_ON_CLICK_LISTENER = refClass(CanonicalNameConstants.VIEW_ON_CLICK_LISTENER);
		public final JClass VIEW_GROUP_LAYOUT_PARAMS = refClass(CanonicalNameConstants.VIEW_GROUP_LAYOUT_PARAMS);
		public final JClass KEY_EVENT = refClass(CanonicalNameConstants.KEY_EVENT);
		public final JClass CONTEXT = refClass(CanonicalNameConstants.CONTEXT);
		public final JClass INTENT = refClass(CanonicalNameConstants.INTENT);
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
		public final JClass ON_LONG_CLICK_LISTENER = refClass(CanonicalNameConstants.ON_LONG_CLICK_LISTENER);
		public final JClass WINDOW = refClass(CanonicalNameConstants.WINDOW);
		public final JClass MENU_ITEM = refClass(CanonicalNameConstants.MENU_ITEM);
		public final JClass MENU_INFLATER = refClass(CanonicalNameConstants.MENU_INFLATER);
		public final JClass MENU = refClass(CanonicalNameConstants.MENU);
		public final JClass ANIMATION_UTILS = refClass(CanonicalNameConstants.ANIMATION_UTILS);
		public final JClass RESOURCES = refClass(CanonicalNameConstants.RESOURCES);
		public final JClass CONFIGURATION = refClass(CanonicalNameConstants.CONFIGURATION);
		public final JClass MOTION_EVENT = refClass(CanonicalNameConstants.MOTION_EVENT);
		public final JClass ON_TOUCH_LISTENER = refClass(CanonicalNameConstants.ON_TOUCH_LISTENER);
		public final JClass HANDLER = refClass(CanonicalNameConstants.HANDLER);
		public final JClass KEY_STORE = refClass(CanonicalNameConstants.KEY_STORE);

		/*
		 * Sherlock
		 */
		public final JClass SHERLOCK_MENU = refClass(CanonicalNameConstants.SHERLOCK_MENU);
		public final JClass SHERLOCK_MENU_ITEM = refClass(CanonicalNameConstants.SHERLOCK_MENU_ITEM);
		public final JClass SHERLOCK_MENU_INFLATER = refClass(CanonicalNameConstants.SHERLOCK_MENU_INFLATER);

		/*
		 * RoboGuice
		 */
		public final JClass INJECTOR_PROVIDER = refClass(CanonicalNameConstants.INJECTOR_PROVIDER);
		public final JClass INJECTOR = refClass(CanonicalNameConstants.INJECTOR);
		public final JClass ON_RESTART_EVENT = refClass(CanonicalNameConstants.ON_RESTART_EVENT);
		public final JClass ON_START_EVENT = refClass(CanonicalNameConstants.ON_START_EVENT);
		public final JClass ON_RESUME_EVENT = refClass(CanonicalNameConstants.ON_RESUME_EVENT);
		public final JClass ON_PAUSE_EVENT = refClass(CanonicalNameConstants.ON_PAUSE_EVENT);
		public final JClass ON_NEW_INTENT_EVENT = refClass(CanonicalNameConstants.ON_NEW_INTENT_EVENT);
		public final JClass EVENT_MANAGER = refClass(CanonicalNameConstants.EVENT_MANAGER);
		public final JClass CONTEXT_SCOPE = refClass(CanonicalNameConstants.CONTEXT_SCOPE);
		public final JClass INJECT = refClass(CanonicalNameConstants.INJECT);
		public final JClass ON_STOP_EVENT = refClass(CanonicalNameConstants.ON_STOP_EVENT);
		public final JClass ON_DESTROY_EVENT = refClass(CanonicalNameConstants.ON_DESTROY_EVENT);
		public final JClass ON_CONFIGURATION_CHANGED_EVENT = refClass(CanonicalNameConstants.ON_CONFIGURATION_CHANGED_EVENT);
		public final JClass ON_CONTENT_CHANGED_EVENT = refClass(CanonicalNameConstants.ON_CONTENT_CHANGED_EVENT);
		public final JClass ON_ACTIVITY_RESULT_EVENT = refClass(CanonicalNameConstants.ON_ACTIVITY_RESULT_EVENT);
		public final JClass ON_CONTENT_VIEW_AVAILABLE_EVENT = refClass(CanonicalNameConstants.ON_CONTENT_VIEW_AVAILABLE_EVENT);
		public final JClass ON_CREATE_EVENT = refClass(CanonicalNameConstants.ON_CREATE_EVENT);

		/*
		 * OrmLite
		 */
		public final JClass CONNECTION_SOURCE = refClass(CanonicalNameConstants.CONNECTION_SOURCE);
		public final JClass OPEN_HELPER_MANAGER = refClass(CanonicalNameConstants.OPEN_HELPER_MANAGER);
		public final JClass DAO_MANAGER = refClass(CanonicalNameConstants.DAO_MANAGER);

		/*
		 * HttpClient
		 */
		public final JClass CLIENT_CONNECTION_MANAGER = refClass(CanonicalNameConstants.CLIENT_CONNECTION_MANAGER);
		public final JClass DEFAULT_HTTP_CLIENT = refClass(CanonicalNameConstants.DEFAULT_HTTP_CLIENT);
		public final JClass SSL_SOCKET_FACTORY = refClass(CanonicalNameConstants.SSL_SOCKET_FACTORY);
		public final JClass SCHEME = refClass(CanonicalNameConstants.SCHEME);
		public final JClass SCHEME_REGISTRY = refClass(CanonicalNameConstants.SCHEME_REGISTRY);
		public final JClass SINGLE_CLIENT_CONN_MANAGER = refClass(CanonicalNameConstants.SINGLE_CLIENT_CONN_MANAGER);

	}

	private final Map<Element, EBeanHolder> eBeanHolders = new HashMap<Element, EBeanHolder>();

	private final JCodeModel codeModel;

	private final Map<String, JClass> loadedClasses = new HashMap<String, JClass>();

	private final Classes classes;

	public EBeansHolder(JCodeModel codeModel) {
		this.codeModel = codeModel;
		classes = new Classes();
	}

	public EBeanHolder create(Element element, Class<? extends Annotation> eBeanAnnotation) {
		EBeanHolder activityHolder = new EBeanHolder(this, eBeanAnnotation);
		eBeanHolders.put(element, activityHolder);
		return activityHolder;
	}

	public EBeanHolder getEBeanHolder(Element element) {
		return eBeanHolders.get(element);
	}

	public JClass refClass(String fullyQualifiedClassName) {

		int arrayCounter = 0;
		while (fullyQualifiedClassName.endsWith("[]")) {
			arrayCounter++;
			fullyQualifiedClassName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.length() - 2);
		}

		JClass refClass = uniqueRefClass(fullyQualifiedClassName);

		for (int i = 0; i < arrayCounter; i++) {
			refClass = refClass.array();
		}

		return refClass;
	}

	/**
	 * Return a unique JClass reference by using {@link JCodeModel#ref(String)}
	 * and keeping a buffer.
	 * 
	 * @param fullyQualifiedClassName
	 * @return
	 */
	private JClass uniqueRefClass(String fullyQualifiedClassName) {
		JClass refClass = loadedClasses.get(fullyQualifiedClassName);
		if (refClass == null) {
			refClass = codeModel.ref(fullyQualifiedClassName);
			loadedClasses.put(fullyQualifiedClassName, refClass);
		}
		return refClass;
	}

	/**
	 * Parse the fully qualified class name and return a JClass instance. This
	 * method support both generics (it'll return a {@link JNarrowedClass}) and
	 * primitives (it'll autobox it to a {@link JClass})
	 * 
	 * @param fullyQualifiedClassName
	 * @return
	 */
	public JClass parseClass(String fullyQualifiedClassName) {
		JClass refClass = loadedClasses.get(fullyQualifiedClassName);
		if (refClass == null) {
			try {
				JType jType = parseType(fullyQualifiedClassName);
				if (jType.isPrimitive()) {
					refClass = jType.boxify();
				}
				refClass = (JClass) jType;
			} catch (Throwable e) {
				refClass = refClass(fullyQualifiedClassName);
			}

			loadedClasses.put(fullyQualifiedClassName, refClass);
		}
		return refClass;
	}

	/**
	 * Obtains a type object from a type name.
	 * 
	 * <p>
	 * This method handles primitive types, arrays, and existing {@link Class}
	 * es.
	 * 
	 * @exception ClassNotFoundException
	 *                If the specified type is not found.
	 */
	private JType parseType(String name) throws ClassNotFoundException {
		// array
		if (name.endsWith("[]")) {
			return parseType(name.substring(0, name.length() - 2)).array();
		}

		// try primitive type
		try {
			return JType.parse(codeModel, name);
		} catch (IllegalArgumentException e) {
			;
		}

		// existing class
		return new TypeNameParser(name).parseTypeName();
	}

	public JClass refClass(Class<?> clazz) {
		return codeModel.ref(clazz);
	}

	public JCodeModel codeModel() {
		return codeModel;
	}

	public Classes classes() {
		return classes;
	}

	private final class TypeNameParser {
		private final String s;
		private int idx;

		public TypeNameParser(String s) {
			this.s = s;
		}

		/**
		 * Parses a type name token T (which can be potentially of the form
		 * Tr&ly;T1,T2,...>, or "? extends/super T".)
		 * 
		 * @return the index of the character next to T.
		 */
		JClass parseTypeName() throws ClassNotFoundException {
			int start = idx;

			if (s.charAt(idx) == '?') {
				// wildcard
				idx++;
				ws();
				String head = s.substring(idx);
				if (head.startsWith("extends")) {
					idx += 7;
					ws();
					return parseTypeName().wildcard();
				} else if (head.startsWith("super")) {
					throw new UnsupportedOperationException("? super T not implemented");
				} else {
					// not supported
					throw new IllegalArgumentException("only extends/super can follow ?, but found " + s.substring(idx));
				}
			}

			while (idx < s.length()) {
				char ch = s.charAt(idx);
				if (Character.isJavaIdentifierStart(ch) || Character.isJavaIdentifierPart(ch) || ch == '.') {
					idx++;
				} else {
					break;
				}
			}

			JClass clazz = uniqueRefClass(s.substring(start, idx));

			return parseSuffix(clazz);
		}

		/**
		 * Parses additional left-associative suffixes, like type arguments and
		 * array specifiers.
		 */
		private JClass parseSuffix(JClass clazz) throws ClassNotFoundException {
			if (idx == s.length()) {
				return clazz; // hit EOL
			}

			char ch = s.charAt(idx);

			if (ch == '<') {
				return parseSuffix(parseArguments(clazz));
			}

			if (ch == '[') {
				if (s.charAt(idx + 1) == ']') {
					idx += 2;
					return parseSuffix(clazz.array());
				}
				throw new IllegalArgumentException("Expected ']' but found " + s.substring(idx + 1));
			}

			return clazz;
		}

		/**
		 * Skips whitespaces
		 */
		private void ws() {
			while (Character.isWhitespace(s.charAt(idx)) && idx < s.length()) {
				idx++;
			}
		}

		/**
		 * Parses '&lt;T1,T2,...,Tn>'
		 * 
		 * @return the index of the character next to '>'
		 */
		private JClass parseArguments(JClass rawType) throws ClassNotFoundException {
			if (s.charAt(idx) != '<') {
				throw new IllegalArgumentException();
			}
			idx++;

			List<JClass> args = new ArrayList<JClass>();

			while (true) {
				args.add(parseTypeName());
				if (idx == s.length()) {
					throw new IllegalArgumentException("Missing '>' in " + s);
				}
				char ch = s.charAt(idx);
				if (ch == '>') {
					return rawType.narrow(args.toArray(new JClass[args.size()]));
				}

				if (ch != ',') {
					throw new IllegalArgumentException(s);
				}
				idx++;
			}

		}
	}

}
