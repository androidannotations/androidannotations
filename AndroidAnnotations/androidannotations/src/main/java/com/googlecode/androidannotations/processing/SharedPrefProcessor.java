/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultFloat;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
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
import com.googlecode.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefField;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class SharedPrefProcessor extends AnnotationHelper implements ElementProcessor {

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

    public SharedPrefProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return SharedPref.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

        TypeElement typeElement = (TypeElement) element;

        String interfaceQualifiedName = typeElement.getQualifiedName().toString();
        String interfaceSimpleName = typeElement.getSimpleName().toString();

        String helperQualifiedName = interfaceQualifiedName + ModelConstants.GENERATION_SUFFIX;
        JDefinedClass helperClass = codeModel._class(JMod.PUBLIC | JMod.FINAL, helperQualifiedName, ClassType.CLASS);

        helperClass._extends(SharedPreferencesHelper.class);

        // Extracting valid methods
        Elements elements = processingEnv.getElementUtils();
        List<? extends Element> inheritedMembers = elements.getAllMembers(typeElement);
        List<ExecutableElement> inheritedMethods = ElementFilter.methodsIn(inheritedMembers);
        List<ExecutableElement> validMethods = new ArrayList<ExecutableElement>();
        for (ExecutableElement method : inheritedMethods) {
            if (!method.getEnclosingElement().asType().toString().equals("java.lang.Object")) {
                validMethods.add(method);
            }
        }

        // Static editor class
        JDefinedClass editorClass = helperClass._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, interfaceSimpleName + "Editor" + ModelConstants.GENERATION_SUFFIX);

        editorClass._extends(codeModel.ref(EditorHelper.class).narrow(editorClass));

        // Editor constructor
        JMethod editorConstructor = editorClass.constructor(JMod.NONE);
        JClass sharedPreferencesClass = codeModel.ref("android.content.SharedPreferences");
        JVar sharedPreferencesParam = editorConstructor.param(sharedPreferencesClass, "sharedPreferences");
        editorConstructor.body().invoke("super").arg(sharedPreferencesParam);

        // Editor field methods
        for (ExecutableElement method : validMethods) {
            String returnType = method.getReturnType().toString();
            EditorFieldHolder editorFieldHolder = EDITOR_FIELD_BY_TYPE.get(returnType);
            JClass editorFieldClass = codeModel.ref(editorFieldHolder.fieldClass);
            String fieldName = method.getSimpleName().toString();
            JMethod editorFieldMethod = editorClass.method(JMod.PUBLIC, editorFieldClass.narrow(editorClass), fieldName);
            editorFieldMethod.body()._return(JExpr.invoke(editorFieldHolder.fieldMethodName).arg(fieldName));
        }

        // Helper constructor
        JClass activityClass = codeModel.ref("android.app.Activity");
        JClass contextClass = codeModel.ref("android.content.Context");

        SharedPref sharedPrefAnnotation = typeElement.getAnnotation(SharedPref.class);
        Scope scope = sharedPrefAnnotation.value();
        int mode = sharedPrefAnnotation.mode();
        JMethod constructor = helperClass.constructor(JMod.PUBLIC);
        switch (scope) {
        case ACTIVITY_DEFAULT: {
            JVar activityParam = constructor.param(activityClass, "activity");
            constructor.body() //
                    .invoke("super") //
                    .arg(activityParam.invoke("getPreferences") //
                            .arg(JExpr.lit(mode)));
            break;
        }
        case ACTIVITY: {
            JVar activityParam = constructor.param(activityClass, "activity");
            constructor.body().invoke("super") //
                    .arg(activityParam.invoke("getSharedPreferences") //
                            .arg(activityParam.invoke("getLocalClassName") //
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
            JClass preferenceManagerClass = codeModel.ref("android.preference.PreferenceManager");
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
            }
            if ("float".equals(returnType)) {
                JExpression defaultValue;
                DefaultFloat defaultAnnotation = method.getAnnotation(DefaultFloat.class);
                if (defaultAnnotation != null) {
                    defaultValue = JExpr.lit(defaultAnnotation.value());
                } else {
                    defaultValue = JExpr.lit(0f);
                }
                addFieldHelperMethod(helperClass, fieldName, defaultValue, FloatPrefField.class, "floatField");
            }
            if ("int".equals(returnType)) {
                JExpression defaultValue;
                DefaultInt defaultAnnotation = method.getAnnotation(DefaultInt.class);
                if (defaultAnnotation != null) {
                    defaultValue = JExpr.lit(defaultAnnotation.value());
                } else {
                    defaultValue = JExpr.lit(0);
                }
                addFieldHelperMethod(helperClass, fieldName, defaultValue, IntPrefField.class, "intField");
            }
            if ("java.lang.String".equals(returnType)) {
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
}
