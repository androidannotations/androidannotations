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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.annotations.Extra;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

public class ExtraProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();
	private final ProcessingEnvironment processingEnv;

	public ExtraProcessor(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Extra.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Extra annotation = element.getAnnotation(Extra.class);
		String extraKey = annotation.value();
		String fieldName = element.getSimpleName().toString();

		if (extraKey.isEmpty()) {
			extraKey = fieldName;
		}

		TypeMirror elementType = element.asType();
		boolean isPrimitive = elementType.getKind().isPrimitive();

		Classes classes = holder.classes();

		if (!isPrimitive && holder.cast == null) {
			generateCastMethod(codeModel, holder);
		}

		if (holder.extras == null) {
			injectExtras(holder, codeModel);
		}

		String staticFieldName;
		if (fieldName.endsWith("Extra")) {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(fieldName);
		} else {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(fieldName + "Extra");
		}

		JFieldVar extraKeyField = holder.generatedClass.field(PUBLIC | STATIC | FINAL, classes.STRING, staticFieldName, lit(extraKey));

		JBlock ifContainsKey = holder.extrasNotNullBlock._if(JExpr.invoke(holder.extras, "containsKey").arg(extraKeyField))._then();

		JTryBlock containsKeyTry = ifContainsKey._try();

		JFieldRef extraField = JExpr.ref(fieldName);

		if (isPrimitive) {
			JPrimitiveType primitiveType = JType.parse(codeModel, elementType.toString());
			JClass wrapperType = primitiveType.boxify();
			containsKeyTry.body().assign(extraField, JExpr.cast(wrapperType, holder.extras.invoke("get").arg(extraKeyField)));
		} else {
			containsKeyTry.body().assign(extraField, JExpr.invoke(holder.cast).arg(holder.extras.invoke("get").arg(extraKeyField)));
		}

		JCatchBlock containsKeyCatch = containsKeyTry._catch(classes.CLASS_CAST_EXCEPTION);
		JVar exceptionParam = containsKeyCatch.param("e");

		JInvocation logError = classes.LOG.staticInvoke("e");

		logError.arg(holder.generatedClass.name());
		logError.arg("Could not cast extra to expected type, the field is left to its default value");
		logError.arg(exceptionParam);

		containsKeyCatch.body().add(logError);

		/*
		 * holder.intentBuilderClass may be null if the annotated component is
		 * an abstract activity
		 */
		if (holder.intentBuilderClass != null) {
			{
				// flags()
				JMethod method = holder.intentBuilderClass.method(PUBLIC, holder.intentBuilderClass, fieldName);

				boolean castToSerializable = false;
				TypeMirror extraType = elementType;
				if (extraType.getKind() == TypeKind.DECLARED) {
					Elements elementUtils = processingEnv.getElementUtils();
					Types typeUtils = processingEnv.getTypeUtils();
					TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
					if (!typeUtils.isSubtype(extraType, parcelableType)) {
						TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
						if (!typeUtils.isSubtype(extraType, stringType)) {
							castToSerializable = true;
						}
					}
				}
				JClass paramClass = helper.typeMirrorToJClass(extraType, holder);
				JVar extraParam = method.param(paramClass, fieldName);
				JBlock body = method.body();
				JInvocation invocation = body.invoke(holder.intentField, "putExtra").arg(extraKeyField);
				if (castToSerializable) {
					invocation.arg(cast(classes.SERIALIZABLE, extraParam));
				} else {
					invocation.arg(extraParam);
				}
				body._return(_this());
			}
		}

	}

	private void generateCastMethod(JCodeModel codeModel, EBeanHolder holder) {
		JType objectType = codeModel._ref(Object.class);
		JMethod method = holder.generatedClass.method(JMod.PRIVATE, objectType, "cast_");
		JTypeVar genericType = method.generify("T");
		method.type(genericType);
		JVar objectParam = method.param(objectType, "object");
		method.annotate(SuppressWarnings.class).param("value", "unchecked");
		method.body()._return(JExpr.cast(genericType, objectParam));
		holder.cast = method;
	}

	/**
	 * Adds call to injectExtras_() in onCreate and setIntent() methods.
	 */
	private void injectExtras(EBeanHolder holder, JCodeModel codeModel) {

		Classes classes = holder.classes();

		JMethod injectExtrasMethod = holder.generatedClass.method(PRIVATE, codeModel.VOID, "injectExtras_");

		overrideSetIntent(holder, codeModel, injectExtrasMethod);

		injectExtrasOnInit(holder, classes.INTENT, injectExtrasMethod);

		JBlock injectExtrasBody = injectExtrasMethod.body();

		JVar intent = injectExtrasBody.decl(classes.INTENT, "intent_", invoke("getIntent"));

		holder.extras = injectExtrasBody.decl(classes.BUNDLE, "extras_");
		holder.extras.init(intent.invoke("getExtras"));

		holder.extrasNotNullBlock = injectExtrasBody._if(holder.extras.ne(_null()))._then();
	}

	private void overrideSetIntent(EBeanHolder holder, JCodeModel codeModel, JMethod initIntentMethod) {
		if (holder.intentBuilderClass != null) {

			JMethod setIntentMethod = holder.generatedClass.method(PUBLIC, codeModel.VOID, "setIntent");
			setIntentMethod.annotate(Override.class);
			JVar methodParam = setIntentMethod.param(holder.classes().INTENT, "newIntent");

			JBlock setIntentBody = setIntentMethod.body();

			setIntentBody.invoke(_super(), setIntentMethod).arg(methodParam);
			setIntentBody.invoke(initIntentMethod);
		}
	}

	private void injectExtrasOnInit(EBeanHolder holder, JClass intentClass, JMethod injectExtrasMethod) {
		holder.initBody.invoke(injectExtrasMethod);
	}
}
