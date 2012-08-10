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

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultFloat;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultLong;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;
import com.googlecode.androidannotations.api.sharedpreferences.BooleanPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.BooleanPrefField;
import com.googlecode.androidannotations.api.sharedpreferences.EditorHelper;
import com.googlecode.androidannotations.api.sharedpreferences.FloatPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.FloatPrefField;
import com.googlecode.androidannotations.api.sharedpreferences.IntPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.IntPrefField;
import com.googlecode.androidannotations.api.sharedpreferences.LongPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.LongPrefField;
import com.googlecode.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefField;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class SharedPrefProcessor implements GeneratingElementProcessor {

	private static class EditorFieldHolder {
		public final Class<?> fieldClass;
		public final String fieldMethodName;

		public EditorFieldHolder(Class<?> fieldClass, String fieldMethodName) {
			this.fieldClass = fieldClass;
			this.fieldMethodName = fieldMethodName;
		}
	}

	private static final Map<String, EditorFieldHolder> EDITOR_FIELD_BY_TYPE = new HashMap<String, EditorFieldHolder>() {
		private static final long serialVersionUID = 1L;
		{
			put("boolean", new EditorFieldHolder(BooleanPrefEditorField.class, "booleanField"));
			put("float", new EditorFieldHolder(FloatPrefEditorField.class, "floatField"));
			put("int", new EditorFieldHolder(IntPrefEditorField.class, "intField"));
			put("long", new EditorFieldHolder(LongPrefEditorField.class, "longField"));
			put("java.lang.String", new EditorFieldHolder(StringPrefEditorField.class, "stringField"));
		}
	};

	@Override
	public Class<? extends Annotation> getTarget() {
		return SharedPref.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		TypeElement typeElement = (TypeElement) element;

		String interfaceQualifiedName = typeElement.getQualifiedName().toString();
		String interfaceSimpleName = typeElement.getSimpleName().toString();

		String helperQualifiedName = interfaceQualifiedName + ModelConstants.GENERATION_SUFFIX;
		JDefinedClass helperClass = codeModel._class(JMod.PUBLIC | JMod.FINAL, helperQualifiedName, ClassType.CLASS);

		helperClass._extends(SharedPreferencesHelper.class);

		// Extracting valid methods
		List<? extends Element> members = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(members);
		List<ExecutableElement> validMethods = new ArrayList<ExecutableElement>();
		for (ExecutableElement method : methods) {
			validMethods.add(method);
		}

		// Static editor class
		JDefinedClass editorClass = helperClass._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, interfaceSimpleName + "Editor" + ModelConstants.GENERATION_SUFFIX);

		editorClass._extends(eBeansHolder.refClass(EditorHelper.class).narrow(editorClass));

		// Editor constructor
		JMethod editorConstructor = editorClass.constructor(JMod.NONE);
		JClass sharedPreferencesClass = eBeansHolder.refClass("android.content.SharedPreferences");
		JVar sharedPreferencesParam = editorConstructor.param(sharedPreferencesClass, "sharedPreferences");
		editorConstructor.body().invoke("super").arg(sharedPreferencesParam);

		// Editor field methods
		for (ExecutableElement method : validMethods) {
			String returnType = method.getReturnType().toString();
			EditorFieldHolder editorFieldHolder = EDITOR_FIELD_BY_TYPE.get(returnType);
			JClass editorFieldClass = eBeansHolder.refClass(editorFieldHolder.fieldClass);
			String fieldName = method.getSimpleName().toString();
			JMethod editorFieldMethod = editorClass.method(JMod.PUBLIC, editorFieldClass.narrow(editorClass), fieldName);
			editorFieldMethod.body()._return(JExpr.invoke(editorFieldHolder.fieldMethodName).arg(fieldName));
		}

		// Helper constructor
		JClass contextClass = eBeansHolder.refClass("android.content.Context");

		SharedPref sharedPrefAnnotation = typeElement.getAnnotation(SharedPref.class);
		Scope scope = sharedPrefAnnotation.value();
		int mode = sharedPrefAnnotation.mode();
		JMethod constructor = helperClass.constructor(JMod.PUBLIC);
		switch (scope) {
		case ACTIVITY_DEFAULT: {

			JVar contextParam = constructor.param(contextClass, "context");
			JMethod getLocalClassName = getLocalClassName(eBeansHolder, helperClass, codeModel);
			constructor.body().invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(invoke(getLocalClassName).arg(contextParam)) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case ACTIVITY: {
			JVar contextParam = constructor.param(contextClass, "context");
			JMethod getLocalClassName = getLocalClassName(eBeansHolder, helperClass, codeModel);
			constructor.body().invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(invoke(getLocalClassName).arg(contextParam) //
									.plus(JExpr.lit("_" + interfaceSimpleName))) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case UNIQUE: {
			JVar contextParam = constructor.param(contextClass, "context");
			constructor.body() //
					.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(JExpr.lit(interfaceSimpleName)) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case APPLICATION_DEFAULT: {
			JClass preferenceManagerClass = eBeansHolder.refClass("android.preference.PreferenceManager");
			JVar contextParam = constructor.param(contextClass, "context");
			constructor.body() //
					.invoke("super") //
					.arg(preferenceManagerClass.staticInvoke("getDefaultSharedPreferences") //
							.arg(contextParam));
			break;
		}
		}

		// Helper edit method
		JMethod editMethod = helperClass.method(JMod.PUBLIC, editorClass, "edit");
		editMethod.body()._return(JExpr._new(editorClass).arg(JExpr.invoke("getSharedPreferences")));

		// Helper field methods
		for (ExecutableElement method : validMethods) {
			String returnType = method.getReturnType().toString();
			String fieldName = method.getSimpleName().toString();
			if ("boolean".equals(returnType)) {
				JExpression defaultValue;
				DefaultBoolean defaultAnnotation = method.getAnnotation(DefaultBoolean.class);
				if (defaultAnnotation != null) {
					defaultValue = JExpr.lit(defaultAnnotation.value());
				} else {
					defaultValue = JExpr.lit(false);
				}
				addFieldHelperMethod(helperClass, fieldName, defaultValue, BooleanPrefField.class, "booleanField");
			} else if ("float".equals(returnType)) {
				JExpression defaultValue;
				DefaultFloat defaultAnnotation = method.getAnnotation(DefaultFloat.class);
				if (defaultAnnotation != null) {
					defaultValue = JExpr.lit(defaultAnnotation.value());
				} else {
					defaultValue = JExpr.lit(0f);
				}
				addFieldHelperMethod(helperClass, fieldName, defaultValue, FloatPrefField.class, "floatField");
			} else if ("int".equals(returnType)) {
				JExpression defaultValue;
				DefaultInt defaultAnnotation = method.getAnnotation(DefaultInt.class);
				if (defaultAnnotation != null) {
					defaultValue = JExpr.lit(defaultAnnotation.value());
				} else {
					defaultValue = JExpr.lit(0);
				}
				addFieldHelperMethod(helperClass, fieldName, defaultValue, IntPrefField.class, "intField");
			} else if ("long".equals(returnType)) {
				JExpression defaultValue;
				DefaultLong defaultAnnotation = method.getAnnotation(DefaultLong.class);
				if (defaultAnnotation != null) {
					defaultValue = JExpr.lit(defaultAnnotation.value());
				} else {
					defaultValue = JExpr.lit(0l);
				}
				addFieldHelperMethod(helperClass, fieldName, defaultValue, LongPrefField.class, "longField");
			} else if ("java.lang.String".equals(returnType)) {
				JExpression defaultValue;
				DefaultString defaultAnnotation = method.getAnnotation(DefaultString.class);
				if (defaultAnnotation != null) {
					defaultValue = JExpr.lit(defaultAnnotation.value());
				} else {
					defaultValue = JExpr.lit("");
				}
				addFieldHelperMethod(helperClass, fieldName, defaultValue, StringPrefField.class, "stringField");
			}
		}

	}

	private void addFieldHelperMethod(JDefinedClass helperClass, String fieldName, JExpression defaultValue, Class<?> prefFieldHelperClass, String fieldHelperMethodName) {
		JMethod fieldMethod = helperClass.method(JMod.PUBLIC, prefFieldHelperClass, fieldName);
		fieldMethod.body()._return(JExpr.invoke(fieldHelperMethodName).arg(fieldName).arg(defaultValue));
	}

	private JMethod getLocalClassName(EBeansHolder eBeansHolder, JDefinedClass helperClass, JCodeModel codeModel) {

		JClass stringClass = eBeansHolder.refClass(String.class);
		JMethod getLocalClassName = helperClass.method(PRIVATE | STATIC, stringClass, "getLocalClassName");
		JClass contextClass = eBeansHolder.refClass("android.content.Context");

		JVar contextParam = getLocalClassName.param(contextClass, "context");

		JBlock body = getLocalClassName.body();

		JVar packageName = body.decl(stringClass, "packageName", contextParam.invoke("getPackageName"));

		JVar className = body.decl(stringClass, "className", contextParam.invoke("getClass").invoke("getName"));

		JVar packageLen = body.decl(codeModel.INT, "packageLen", packageName.invoke("length"));

		JExpression condition = className.invoke("startsWith").arg(packageName).not() //
				.cor(className.invoke("length").lte(packageLen)) //
				.cor(className.invoke("charAt").arg(packageLen).ne(lit('.')));

		body._if(condition)._then()._return(className);

		body._return(className.invoke("substring").arg(packageLen.plus(lit(1))));

		return getLocalClassName;
	}
}
