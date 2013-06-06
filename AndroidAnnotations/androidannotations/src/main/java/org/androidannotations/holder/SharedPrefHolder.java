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
package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.api.sharedpreferences.*;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;

public class SharedPrefHolder extends BaseGeneratedClassHolder {

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
			put(CanonicalNameConstants.STRING, new EditorFieldHolder(StringPrefEditorField.class, "stringField"));
		}
	};

	private JBlock constructorSuperBlock;
	private JVar constructorContextParam;
	private JFieldVar contextField;
	private JDefinedClass editorClass;

	public SharedPrefHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);

		createEditorClass();
		createEditMethod();
	}

	@Override
	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		String subComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;
		generatedClass = codeModel()._class(PUBLIC | FINAL, subComponentQualifiedName, ClassType.CLASS);
		generatedClass._extends(SharedPreferencesHelper.class);
	}

	private void createEditorClass() throws JClassAlreadyExistsException {
		String interfaceSimpleName = annotatedElement.getSimpleName().toString();
		editorClass = generatedClass._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, interfaceSimpleName + "Editor" + ModelConstants.GENERATION_SUFFIX);
		editorClass._extends(processHolder.refClass(EditorHelper.class).narrow(editorClass));

		createEditorConstructor();
	}

	private void createEditorConstructor() {
		JMethod editorConstructor = editorClass.constructor(JMod.NONE);
		JClass sharedPreferencesClass = processHolder.refClass("android.content.SharedPreferences");
		JVar sharedPreferencesParam = editorConstructor.param(sharedPreferencesClass, "sharedPreferences");
		editorConstructor.body().invoke("super").arg(sharedPreferencesParam);
	}

	private void createEditMethod() {
		JMethod editMethod = generatedClass.method(JMod.PUBLIC, editorClass, "edit");
		editMethod.body()._return(JExpr._new(editorClass).arg(JExpr.invoke("getSharedPreferences")));
	}

	public void createFieldMethod(Class<?> prefFieldHelperClass, String fieldName, String fieldHelperMethodName, JExpression defaultValue) {
		JMethod fieldMethod = generatedClass.method(JMod.PUBLIC, prefFieldHelperClass, fieldName);
		fieldMethod.body()._return(JExpr.invoke(fieldHelperMethodName).arg(fieldName).arg(defaultValue));
	}

	public void createEditorFieldMethods(ExecutableElement method) {
		String returnType = method.getReturnType().toString();
		EditorFieldHolder editorFieldHolder = EDITOR_FIELD_BY_TYPE.get(returnType);
		JClass editorFieldClass = processHolder.refClass(editorFieldHolder.fieldClass);
		String fieldName = method.getSimpleName().toString();
		JMethod editorFieldMethod = editorClass.method(JMod.PUBLIC, editorFieldClass.narrow(editorClass), fieldName);
		editorFieldMethod.body()._return(JExpr.invoke(editorFieldHolder.fieldMethodName).arg(fieldName));
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

	private void setConstructor() {
		JMethod constructor = generatedClass.constructor(JMod.PUBLIC);
		constructorContextParam = constructor.param(classes().CONTEXT, "context");
		JBlock constructorBody = constructor.body();
		constructorSuperBlock = constructorBody.block();
		constructorBody.assign(JExpr._this().ref(getContextField()), constructorContextParam);
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			setContextField();
		}
		return contextField;
	}

	protected void setContextField() {
		contextField = generatedClass.field(JMod.PRIVATE, classes().CONTEXT, "context_");
	}
}
