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

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.ResId;
import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.DefaultStringSet;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.api.sharedpreferences.AbstractPrefField;
import org.androidannotations.api.sharedpreferences.BooleanPrefField;
import org.androidannotations.api.sharedpreferences.FloatPrefField;
import org.androidannotations.api.sharedpreferences.IntPrefField;
import org.androidannotations.api.sharedpreferences.LongPrefField;
import org.androidannotations.api.sharedpreferences.StringPrefField;
import org.androidannotations.api.sharedpreferences.StringSetPrefField;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.IdValidatorHelper.FallbackStrategy;
import org.androidannotations.holder.SharedPrefHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import org.androidannotations.rclass.IRInnerClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class SharedPrefHandler extends BaseGeneratingAnnotationHandler<SharedPrefHolder> {

	private IdAnnotationHelper annotationHelper;
	private APTCodeModelHelper aptCodeModelHelper;

	private static final class DefaultPrefInfo<T> {
		final Class<? extends Annotation> annotationClass;
		final Class<? extends AbstractPrefField<?>> prefFieldClass;
		final IRClass.Res resType;
		final T defaultValue;
		final String fieldHelperMethodName;

		DefaultPrefInfo(Class<? extends Annotation> annotationClass, Class<? extends AbstractPrefField<?>> prefFieldClass, Res resType, T defaultValue, String fieldHelperMethodName) {
			this.annotationClass = annotationClass;
			this.prefFieldClass = prefFieldClass;
			this.resType = resType;
			this.defaultValue = defaultValue;
			this.fieldHelperMethodName = fieldHelperMethodName;
		}
	}

	private static final Map<String, DefaultPrefInfo<?>> DEFAULT_PREF_INFOS = new HashMap<String, SharedPrefHandler.DefaultPrefInfo<?>>() {
		private static final long serialVersionUID = 1L;
		{
			put("boolean", new DefaultPrefInfo<>(DefaultBoolean.class, BooleanPrefField.class, IRClass.Res.BOOL, false, "booleanField"));
			put("float", new DefaultPrefInfo<>(DefaultFloat.class, FloatPrefField.class, IRClass.Res.INTEGER, 0f, "floatField"));
			put("int", new DefaultPrefInfo<>(DefaultInt.class, IntPrefField.class, IRClass.Res.INTEGER, 0, "intField"));
			put("long", new DefaultPrefInfo<>(DefaultLong.class, LongPrefField.class, IRClass.Res.INTEGER, 0L, "longField"));
			put(CanonicalNameConstants.STRING, new DefaultPrefInfo<>(DefaultString.class, StringPrefField.class, IRClass.Res.STRING, "", "stringField"));
			put(CanonicalNameConstants.STRING_SET, new DefaultPrefInfo<Set<String>>(DefaultStringSet.class, StringSetPrefField.class, null, null, "stringSetField"));
		}
	};

	public SharedPrefHandler(ProcessingEnvironment processingEnvironment) {
		super(SharedPref.class, processingEnvironment);
		aptCodeModelHelper = new APTCodeModelHelper();
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
		super.validate(element, validatedElements, valid);

		TypeElement typeElement = (TypeElement) element;

		validatorHelper.isInterface(typeElement, valid);

		List<? extends Element> inheritedMembers = processingEnv.getElementUtils().getAllMembers(typeElement);

		for (Element memberElement : inheritedMembers) {
			if (!memberElement.getEnclosingElement().asType().toString().equals("java.lang.Object")) {
				validatorHelper.isPrefMethod(memberElement, valid);

				DefaultPrefInfo<?> info = null;
				IdValidatorHelper defaultAnnotationValidatorHelper = null;

				if (valid.isValid()) {
					info = DEFAULT_PREF_INFOS.get(((ExecutableElement) memberElement).getReturnType().toString());
					validatorHelper.hasCorrectDefaultAnnotation((ExecutableElement) memberElement, valid);

					if (valid.isValid() && memberElement.getAnnotation(DefaultRes.class) != null) {
						defaultAnnotationValidatorHelper = new IdValidatorHelper(new IdAnnotationHelper(processingEnv, DefaultRes.class.getName(), rClass));
						defaultAnnotationValidatorHelper.resIdsExist(memberElement, info.resType, FallbackStrategy.USE_ELEMENT_NAME, valid);
					} else if (valid.isValid() && memberElement.getAnnotation(info.annotationClass) != null) {
						defaultAnnotationValidatorHelper = new IdValidatorHelper(new IdAnnotationHelper(processingEnv, info.annotationClass.getName(), rClass));
					}

					if (valid.isValid() && defaultAnnotationValidatorHelper != null) {
						defaultAnnotationValidatorHelper.annotationParameterIsOptionalValidResId(memberElement, IRClass.Res.STRING, "keyRes", valid);
					}
				}
			}
		}
	}

	@Override
	public void process(Element element, SharedPrefHolder holder) {
		generateConstructor(element, holder);
		generateFieldMethodAndEditorFieldMethod(element, holder);
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
							.arg(lit(mode)));
			break;
		}
		case ACTIVITY: {
			JMethod getLocalClassName = getLocalClassName(holder);
			constructorSuperBlock.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(invoke(getLocalClassName).arg(contextParam) //
									.plus(lit("_" + interfaceSimpleName))) //
							.arg(lit(mode)));
			break;
		}
		case UNIQUE: {
			constructorSuperBlock.invoke("super") //
					.arg(contextParam.invoke("getSharedPreferences") //
							.arg(lit(interfaceSimpleName)) //
							.arg(lit(mode)));
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
			JExpression keyExpression = generateFieldMethod(sharedPrefHolder, method);
			sharedPrefHolder.createEditorFieldMethods(method, keyExpression);
		}
	}

	private List<ExecutableElement> getValidMethods(Element element) {
		List<? extends Element> members = element.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(members);
		List<ExecutableElement> validMethods = new ArrayList<>();
		for (ExecutableElement method : methods) {
			validMethods.add(method);
		}
		return validMethods;
	}

	private JExpression generateFieldMethod(SharedPrefHolder holder, ExecutableElement method) {
		DefaultPrefInfo<?> info = DEFAULT_PREF_INFOS.get(method.getReturnType().toString());
		return createFieldMethod(holder, method, info.annotationClass, info.prefFieldClass, info.defaultValue, info.resType, info.fieldHelperMethodName);
	}

	private JExpression createFieldMethod(SharedPrefHolder holder, ExecutableElement method, Class<? extends Annotation> annotationClass, Class<? extends AbstractPrefField<?>> prefFieldClass,
			Object defaultValue, Res resType, String fieldHelperMethodName) {
		Annotation annotation = method.getAnnotation(annotationClass);
		JExpression defaultValueExpr;

		if (annotation != null && method.getAnnotation(DefaultStringSet.class) == null) {
			defaultValueExpr = aptCodeModelHelper.litObject(annotationHelper.extractAnnotationParameter(method, annotationClass.getName(), "value"));
		} else if (method.getAnnotation(DefaultRes.class) != null) {
			defaultValueExpr = extractResValue(holder, method, resType);
			annotationClass = DefaultRes.class;
		} else if (method.getAnnotation(DefaultStringSet.class) != null) {
			defaultValueExpr = newEmptyStringHashSet();
			annotationClass = DefaultStringSet.class;
		} else {
			defaultValueExpr = defaultValue != null ? aptCodeModelHelper.litObject(defaultValue) : newEmptyStringHashSet();
			annotationClass = null;
		}

		Integer keyResId = ResId.DEFAULT_VALUE;

		if (annotationClass != null) {
			keyResId = annotationHelper.extractAnnotationParameter(method, annotationClass.getName(), "keyRes");
		}

		JExpression keyExpression;
		String fieldName = method.getSimpleName().toString();

		if (keyResId == ResId.DEFAULT_VALUE) {
			keyExpression = lit(fieldName);
		} else {
			IRInnerClass idClass = rClass.get(IRClass.Res.STRING);
			JFieldRef idRef = idClass.getIdStaticRef(keyResId, processHolder);
			keyExpression = holder.getEditorContextField().invoke("getString").arg(idRef);
		}

		holder.createFieldMethod(prefFieldClass, keyExpression, fieldName, fieldHelperMethodName, defaultValueExpr);
		return keyExpression;
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

	private JExpression newEmptyStringHashSet() {
		return JExpr._new(classes().HASH_SET.narrow(classes().STRING)).arg(lit(0));
	}

}
