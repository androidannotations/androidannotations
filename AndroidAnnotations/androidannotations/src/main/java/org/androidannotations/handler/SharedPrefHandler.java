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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.STATIC;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.api.sharedpreferences.AbstractPrefEditorField;
import org.androidannotations.api.sharedpreferences.AbstractPrefField;
import org.androidannotations.api.sharedpreferences.BooleanPrefEditorField;
import org.androidannotations.api.sharedpreferences.BooleanPrefField;
import org.androidannotations.api.sharedpreferences.EditorHelper;
import org.androidannotations.api.sharedpreferences.FloatPrefEditorField;
import org.androidannotations.api.sharedpreferences.FloatPrefField;
import org.androidannotations.api.sharedpreferences.IntPrefEditorField;
import org.androidannotations.api.sharedpreferences.IntPrefField;
import org.androidannotations.api.sharedpreferences.LongPrefEditorField;
import org.androidannotations.api.sharedpreferences.LongPrefField;
import org.androidannotations.api.sharedpreferences.SharedPreferencesCompat;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.api.sharedpreferences.StringPrefEditorField;
import org.androidannotations.api.sharedpreferences.StringPrefField;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.holder.SharedPrefHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class SharedPrefHandler extends BaseAnnotationHandler<SharedPrefHolder> implements GeneratingAnnotationHandler<SharedPrefHolder> {

	private IdAnnotationHelper annotationHelper;

	public SharedPrefHandler(ProcessingEnvironment processingEnvironment) {
		super(SharedPref.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public SharedPrefHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new SharedPrefHolder(processHolder, annotatedElement);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		validatorHelper.isInterface(typeElement, valid);

		List<? extends Element> inheritedMembers = processingEnv.getElementUtils().getAllMembers(typeElement);

		for (Element memberElement : inheritedMembers) {
			if (!memberElement.getEnclosingElement().asType().toString().equals("java.lang.Object")) {
				validatorHelper.isPrefMethod(memberElement, valid);
				if (valid.isValid()) {
					validatorHelper.hasCorrectDefaultAnnotation((ExecutableElement) memberElement, valid);
				}
			}
		}
	}

	@Override
	public void process(Element element, SharedPrefHolder holder) {
		generateApiClasses(element);
		generateConstructor(element, holder);
		generateFieldMethodAndEditorFieldMethod(element, holder);
	}

	private void generateApiClasses(Element originatingElement) {
		generateApiClass(originatingElement, AbstractPrefEditorField.class);
		generateApiClass(originatingElement, AbstractPrefField.class);
		generateApiClass(originatingElement, BooleanPrefEditorField.class);
		generateApiClass(originatingElement, BooleanPrefField.class);
		generateApiClass(originatingElement, EditorHelper.class);
		generateApiClass(originatingElement, FloatPrefEditorField.class);
		generateApiClass(originatingElement, FloatPrefField.class);
		generateApiClass(originatingElement, IntPrefEditorField.class);
		generateApiClass(originatingElement, IntPrefField.class);
		generateApiClass(originatingElement, LongPrefEditorField.class);
		generateApiClass(originatingElement, LongPrefField.class);
		generateApiClass(originatingElement, SharedPreferencesCompat.class);
		generateApiClass(originatingElement, SharedPreferencesHelper.class);
		generateApiClass(originatingElement, StringPrefEditorField.class);
		generateApiClass(originatingElement, StringPrefField.class);
	}

	private void generateConstructor(Element element, SharedPrefHolder holder) {
		SharedPref sharedPrefAnnotation = element.getAnnotation(SharedPref.class);
		SharedPref.Scope scope = sharedPrefAnnotation.value();
		int mode = sharedPrefAnnotation.mode();

		String interfaceSimpleName = element.getSimpleName().toString();
		JBlock constructorSuperBlock = holder.getConstructorSuperBlock();
		JVar contextParam = holder.getConstructorContextParam();

		switch (scope) {
		case ACTIVITY_DEFAULT: {
			JMethod getLocalClassName = getLocalClassName(holder);
			constructorSuperBlock.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(invoke(getLocalClassName).arg(contextParam)) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case ACTIVITY: {
			JMethod getLocalClassName = getLocalClassName(holder);
			constructorSuperBlock.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(invoke(getLocalClassName).arg(contextParam) //
									.plus(JExpr.lit("_" + interfaceSimpleName))) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case UNIQUE: {
			constructorSuperBlock.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(JExpr.lit(interfaceSimpleName)) //
							.arg(JExpr.lit(mode)));
			break;
		}
		case APPLICATION_DEFAULT: {
			JClass preferenceManagerClass = refClass("android.preference.PreferenceManager");
			constructorSuperBlock.invoke("super") //
					.arg(preferenceManagerClass.staticInvoke("getDefaultSharedPreferences") //
							.arg(contextParam));
			break;
		}
		}
	}

	private JMethod getLocalClassName(SharedPrefHolder holder) {

		JClass stringClass = classes().STRING;
		JMethod getLocalClassName = holder.getGeneratedClass().method(PRIVATE | STATIC, stringClass, "getLocalClassName");
		JClass contextClass = classes().CONTEXT;

		JVar contextParam = getLocalClassName.param(contextClass, "context");

		JBlock body = getLocalClassName.body();

		JVar packageName = body.decl(stringClass, "packageName", contextParam.invoke("getPackageName"));

		JVar className = body.decl(stringClass, "className", contextParam.invoke("getClass").invoke("getName"));

		JVar packageLen = body.decl(codeModel().INT, "packageLen", packageName.invoke("length"));

		JExpression condition = className.invoke("startsWith").arg(packageName).not() //
				.cor(className.invoke("length").lte(packageLen)) //
				.cor(className.invoke("charAt").arg(packageLen).ne(lit('.')));

		body._if(condition)._then()._return(className);

		body._return(className.invoke("substring").arg(packageLen.plus(lit(1))));

		return getLocalClassName;
	}

	private void generateFieldMethodAndEditorFieldMethod(Element element, SharedPrefHolder sharedPrefHolder) {
		for (ExecutableElement method : getValidMethods(element)) {
			generateFieldMethod(sharedPrefHolder, method);
			sharedPrefHolder.createEditorFieldMethods(method);
		}
	}

	private List<ExecutableElement> getValidMethods(Element element) {
		List<? extends Element> members = element.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(members);
		List<ExecutableElement> validMethods = new ArrayList<ExecutableElement>();
		for (ExecutableElement method : methods) {
			validMethods.add(method);
		}
		return validMethods;
	}

	private void generateFieldMethod(SharedPrefHolder holder, ExecutableElement method) {
		String returnType = method.getReturnType().toString();
		JExpression defaultValue = null;
		if ("boolean".equals(returnType)) {
			DefaultBoolean defaultAnnotation = method.getAnnotation(DefaultBoolean.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.BOOL, JExpr.lit(false), BooleanPrefField.class, "booleanField");
		} else if ("float".equals(returnType)) {
			DefaultFloat defaultAnnotation = method.getAnnotation(DefaultFloat.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0f), FloatPrefField.class, "floatField");
		} else if ("int".equals(returnType)) {
			DefaultInt defaultAnnotation = method.getAnnotation(DefaultInt.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0), IntPrefField.class, "intField");
		} else if ("long".equals(returnType)) {
			DefaultLong defaultAnnotation = method.getAnnotation(DefaultLong.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0l), LongPrefField.class, "longField");
		} else if (CanonicalNameConstants.STRING.equals(returnType)) {
			DefaultString defaultAnnotation = method.getAnnotation(DefaultString.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.STRING, JExpr.lit(""), StringPrefField.class, "stringField");
		}
	}

	private void createFieldMethod(SharedPrefHolder holder, ExecutableElement method, JExpression defaultAnnotationValue, IRClass.Res res, JExpression defValue, Class<?> booleanPrefFieldClass, String fieldHelperMethodName) {
		JExpression defaultValue = defaultAnnotationValue;
		if (defaultAnnotationValue == null) {
			if (method.getAnnotation(DefaultRes.class) != null) {
				defaultValue = extractResValue(holder, method, res);
			} else {
				defaultValue = defValue;
			}
		}
		String fieldName = method.getSimpleName().toString();
		holder.createFieldMethod(booleanPrefFieldClass, fieldName, fieldHelperMethodName, defaultValue);
	}

	private JExpression extractResValue(SharedPrefHolder holder, Element method, IRClass.Res res) {
		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(processHolder, method, DefaultRes.class.getCanonicalName(), res, true);

		String resourceGetMethodName = null;
		switch (res) {
		case BOOL:
			resourceGetMethodName = "getBoolean";
			break;
		case INTEGER:
			resourceGetMethodName = "getInteger";
			break;
		case STRING:
			resourceGetMethodName = "getString";
			break;
		default:
			break;
		}
		return holder.getContextField().invoke("getResources").invoke(resourceGetMethodName).arg(idRef);
	}
}
