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

package org.androidannotations.internal.core.helper;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.ref;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.SERIALIZABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.Pair;
import org.androidannotations.holder.HasIntentBuilder;
import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public abstract class IntentBuilder {

	protected AndroidAnnotationsEnvironment environment;
	protected HasIntentBuilder holder;
	protected AndroidManifest androidManifest;
	protected JDefinedClass builderClass;
	protected JFieldRef contextField;
	protected JFieldRef intentField;
	protected AbstractJClass contextClass;
	protected AbstractJClass intentClass;
	protected Map<Pair<TypeMirror, String>, JMethod> putExtraMethods = new HashMap<>();

	protected Elements elementUtils;
	protected Types typeUtils;
	protected APTCodeModelHelper codeModelHelper;

	public IntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		this.environment = holder.getEnvironment();
		this.holder = holder;
		this.androidManifest = androidManifest;
		codeModelHelper = new APTCodeModelHelper(environment);
		elementUtils = environment.getProcessingEnvironment().getElementUtils();
		typeUtils = environment.getProcessingEnvironment().getTypeUtils();
		contextClass = environment.getClasses().CONTEXT;
		intentClass = environment.getClasses().INTENT;
	}

	public void build() throws JClassAlreadyExistsException {
		createClass();
		createContextConstructor();
		createIntentMethod();
	}

	private void createClass() throws JClassAlreadyExistsException {
		builderClass = holder.getGeneratedClass()._class(PUBLIC | STATIC, "IntentBuilder" + generationSuffix());
		builderClass._extends(getSuperClass());
		holder.setIntentBuilderClass(builderClass);
		contextField = ref("context");
		intentField = ref("intent");
	}

	private void createContextConstructor() {
		IJExpression generatedClass = holder.getGeneratedClass().dotclass();
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorContextParam = constructor.param(getClasses().CONTEXT, "context");
		constructor.body().invoke("super").arg(constructorContextParam).arg(generatedClass);
	}

	private void createIntentMethod() {
		JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
		JVar contextParam = method.param(contextClass, "context");
		method.body()._return(_new(holder.getIntentBuilderClass()).arg(contextParam));
	}

	public JMethod getPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField, String docComment) {
		Pair<TypeMirror, String> signature = new Pair<>(elementType, parameterName);
		JMethod putExtraMethod = putExtraMethods.get(signature);
		if (putExtraMethod == null) {
			putExtraMethod = addPutExtraMethod(elementType, parameterName, extraKeyField, docComment);
			putExtraMethods.put(signature, putExtraMethod);
		}
		return putExtraMethod;
	}

	private JMethod addPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField, String docComment) {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), parameterName);
		AbstractJClass parameterClass = codeModelHelper.typeMirrorToJClass(elementType);
		JVar extraParameterVar = method.param(parameterClass, parameterName);
		JInvocation superCall = getSuperPutExtraInvocation(elementType, extraParameterVar, extraKeyField);
		method.body()._return(superCall);
		codeModelHelper.addTrimmedDocComment(method, docComment);
		method.javadoc().addParam(parameterName).append("the extra value");
		method.javadoc().addReturn().append("the IntentBuilder to chain calls");
		return method;
	}

	public JInvocation getSuperPutExtraInvocation(TypeMirror elementType, JVar extraParam, JFieldVar extraKeyField) {
		IJExpression extraParameterArg = extraParam;
		// Cast to Parcelable or Serializable if needed
		if (elementType.getKind() == TypeKind.DECLARED) {
			Elements elementUtils = environment.getProcessingEnvironment().getElementUtils();
			TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
			if (!typeUtils.isSubtype(elementType, parcelableType)) {
				TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
				if (!typeUtils.isSubtype(elementType, stringType)) {
					extraParameterArg = cast(environment.getClasses().SERIALIZABLE, extraParameterArg);
				}
			} else {
				TypeMirror serializableType = elementUtils.getTypeElement(SERIALIZABLE).asType();
				if (typeUtils.isSubtype(elementType, serializableType)) {
					extraParameterArg = cast(environment.getClasses().PARCELABLE, extraParameterArg);
				}
			}
		}
		return _super().invoke("extra").arg(extraKeyField).arg(extraParameterArg);
	}

	protected abstract AbstractJClass getSuperClass();

	protected ProcessHolder.Classes getClasses() {
		return environment.getClasses();
	}

	protected AbstractJClass getJClass(Class<?> clazz) {
		return environment.getJClass(clazz);
	}
}
