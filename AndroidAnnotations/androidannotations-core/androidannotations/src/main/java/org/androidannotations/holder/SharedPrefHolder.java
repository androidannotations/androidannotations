/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.classSuffix;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.api.sharedpreferences.BooleanPrefEditorField;
import org.androidannotations.api.sharedpreferences.EditorHelper;
import org.androidannotations.api.sharedpreferences.FloatPrefEditorField;
import org.androidannotations.api.sharedpreferences.IntPrefEditorField;
import org.androidannotations.api.sharedpreferences.LongPrefEditorField;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.api.sharedpreferences.StringPrefEditorField;
import org.androidannotations.api.sharedpreferences.StringPrefField;
import org.androidannotations.api.sharedpreferences.StringSetPrefEditorField;
import org.androidannotations.helper.CanonicalNameConstants;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class SharedPrefHolder extends BaseGeneratedClassHolder {

	private static class EditorFieldHolder {
		public final Class<?> fieldClass;
		public final String fieldMethodName;

		EditorFieldHolder(Class<?> fieldClass, String fieldMethodName) {
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
			put(CanonicalNameConstants.STRING, new EditorFieldHolder(StringPrefEditorField.class, "stringField"));
			put(CanonicalNameConstants.STRING_SET, new EditorFieldHolder(StringSetPrefEditorField.class, "stringSetField"));
		}
	};

	private JMethod constructor;
	private JBlock constructorSuperBlock;
	private JVar constructorContextParam;
	private JFieldVar contextField;
	private JDefinedClass editorClass;
	private JFieldVar editorContextField;
	private JMethod editorConstructor;
	private JInvocation editMethodEditorInvocation;

	public SharedPrefHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		createEditorClass();
		createEditMethod();
	}

	@Override
	protected void setExtends() {
		generatedClass._extends(SharedPreferencesHelper.class);
	}

	private void createEditorClass() throws JClassAlreadyExistsException {
		String interfaceSimpleName = annotatedElement.getSimpleName().toString();
		editorClass = generatedClass._class(PUBLIC | STATIC | FINAL, interfaceSimpleName + "Editor" + classSuffix());
		editorClass._extends(getJClass(EditorHelper.class).narrow(editorClass));

		createEditorConstructor();
	}

	private void createEditorConstructor() {
		editorConstructor = editorClass.constructor(JMod.NONE);
		AbstractJClass sharedPreferencesClass = getJClass("android.content.SharedPreferences");
		JVar sharedPreferencesParam = editorConstructor.param(sharedPreferencesClass, "sharedPreferences");
		editorConstructor.body().invoke("super").arg(sharedPreferencesParam);
	}

	private void createEditMethod() {
		JMethod editMethod = generatedClass.method(PUBLIC, editorClass, "edit");
		editMethodEditorInvocation = JExpr._new(editorClass).arg(JExpr.invoke("getSharedPreferences"));
		editMethod.body()._return(editMethodEditorInvocation);
	}

	public void createFieldMethod(Class<?> prefFieldHelperClass, IJExpression keyExpression, String fieldName, String fieldHelperMethodName, IJExpression defaultValue, String docComment,
			String defaultValueStr) {
		JMethod fieldMethod = generatedClass.method(PUBLIC, prefFieldHelperClass, fieldName);

		if (defaultValueStr != null) {
			boolean isStringPrefField = StringPrefField.class == prefFieldHelperClass;

			final String defaultValueJavaDoc;
			if (isStringPrefField) {
				defaultValueJavaDoc = "\"" + defaultValueStr + "\"";
			} else {
				defaultValueJavaDoc = defaultValueStr;
			}

			fieldMethod.javadoc().append("<p><b>Defaults to</b>: " + defaultValueJavaDoc + "</p>\n");
		}
		codeModelHelper.addTrimmedDocComment(fieldMethod, docComment);
		fieldMethod.javadoc().addReturn().append("a {@link " + prefFieldHelperClass.getSimpleName() + "} instance to retrieve or write the pref value");
		fieldMethod.body()._return(JExpr.invoke(fieldHelperMethodName).arg(keyExpression).arg(defaultValue));
	}

	public void createEditorFieldMethods(ExecutableElement method, IJExpression keyExpression) {
		String returnType = method.getReturnType().toString();
		EditorFieldHolder editorFieldHolder = EDITOR_FIELD_BY_TYPE.get(returnType);
		AbstractJClass editorFieldClass = getJClass(editorFieldHolder.fieldClass);
		String fieldName = method.getSimpleName().toString();
		JMethod editorFieldMethod = editorClass.method(PUBLIC, editorFieldClass.narrow(editorClass), fieldName);
		String docComment = getProcessingEnvironment().getElementUtils().getDocComment(method);
		codeModelHelper.addTrimmedDocComment(editorFieldMethod, docComment);
		editorFieldMethod.body()._return(JExpr.invoke(editorFieldHolder.fieldMethodName).arg(keyExpression));
	}

	public JBlock getConstructorSuperBlock() {
		if (constructorSuperBlock == null) {
			setConstructor();
		}
		return constructorSuperBlock;
	}

	public JVar getConstructorContextParam() {
		if (constructorContextParam == null) {
			setConstructor();
		}
		return constructorContextParam;
	}

	public JMethod getConstructor() {
		if (constructor == null) {
			setConstructor();
		}
		return constructor;
	}

	private void setConstructor() {
		constructor = generatedClass.constructor(PUBLIC);
		constructorContextParam = constructor.param(getClasses().CONTEXT, "context");
		JBlock constructorBody = constructor.body();
		constructorSuperBlock = constructorBody.blockSimple();
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			setContextField();
		}
		return contextField;
	}

	protected void setContextField() {
		contextField = generatedClass.field(JMod.PRIVATE, getClasses().CONTEXT, "context" + generationSuffix());
		getConstructor().body().assign(JExpr._this().ref(contextField), getConstructorContextParam());
	}

	public JFieldVar getEditorContextField() {
		if (editorContextField == null) {
			setEditorContextField();
		}
		return editorContextField;
	}

	protected void setEditorContextField() {
		editorContextField = editorClass.field(JMod.PRIVATE, getClasses().CONTEXT, "context" + generationSuffix());
		JVar contextParam = editorConstructor.param(getClasses().CONTEXT, "context");
		editorConstructor.body().assign(JExpr._this().ref(editorContextField), contextParam);
		editMethodEditorInvocation.arg(getContextField());
	}
}
