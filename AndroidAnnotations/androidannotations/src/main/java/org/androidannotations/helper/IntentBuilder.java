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

package org.androidannotations.helper;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
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

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public abstract class IntentBuilder {

	protected HasIntentBuilder holder;
	protected AndroidManifest androidManifest;
	protected JDefinedClass builderClass;
	protected JFieldRef contextField;
	protected JFieldRef intentField;
	protected JClass contextClass;
	protected JClass intentClass;
	protected Map<Pair<TypeMirror, String>, JMethod> putExtraMethods = new HashMap<>();

	protected Elements elementUtils;
	protected Types typeUtils;
	protected APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public IntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		this.holder = holder;
		this.androidManifest = androidManifest;
		elementUtils = holder.processingEnvironment().getElementUtils();
		typeUtils = holder.processingEnvironment().getTypeUtils();
		contextClass = holder.classes().CONTEXT;
		intentClass = holder.classes().INTENT;
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
		JExpression generatedClass = holder.getGeneratedClass().dotclass();
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorContextParam = constructor.param(holder.classes().CONTEXT, "context");
		constructor.body().invoke("super").arg(constructorContextParam).arg(generatedClass);
	}

	private void createIntentMethod() {
		JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
		JVar contextParam = method.param(contextClass, "context");
		method.body()._return(_new(holder.getIntentBuilderClass()).arg(contextParam));
	}

	public JMethod getPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField) {
		Pair<TypeMirror, String> signature = new Pair<>(elementType, parameterName);
		JMethod putExtraMethod = putExtraMethods.get(signature);
		if (putExtraMethod == null) {
			putExtraMethod = addPutExtraMethod(elementType, parameterName, extraKeyField);
			putExtraMethods.put(signature, putExtraMethod);
		}
		return putExtraMethod;
	}

	private JMethod addPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField) {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), parameterName);
		JClass parameterClass = codeModelHelper.typeMirrorToJClass(elementType, holder);
		JVar extraParameterVar = method.param(parameterClass, parameterName);
		JInvocation superCall = getSuperPutExtraInvocation(elementType, extraParameterVar, extraKeyField);
		method.body()._return(superCall);
		return method;
	}

	public JInvocation getSuperPutExtraInvocation(TypeMirror elementType, JVar extraParam, JFieldVar extraKeyField) {
		JExpression extraParameterArg = extraParam;
		// Cast to Parcelable or Serializable if needed
		if (elementType.getKind() == TypeKind.DECLARED) {
			Elements elementUtils = holder.processingEnvironment().getElementUtils();
			TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
			if (!typeUtils.isSubtype(elementType, parcelableType)) {
				TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
				if (!typeUtils.isSubtype(elementType, stringType)) {
					extraParameterArg = cast(holder.classes().SERIALIZABLE, extraParameterArg);
				}
			} else {
				TypeMirror serializableType = elementUtils.getTypeElement(SERIALIZABLE).asType();
				if (typeUtils.isSubtype(elementType, serializableType)) {
					extraParameterArg = cast(holder.classes().PARCELABLE, extraParameterArg);
				}
			}
		}
		return _super().invoke("extra").arg(extraKeyField).arg(extraParameterArg);
	}

	protected abstract JClass getSuperClass();
}
