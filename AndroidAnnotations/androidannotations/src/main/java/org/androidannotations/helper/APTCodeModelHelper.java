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
package org.androidannotations.helper;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.SERIALIZABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

public class APTCodeModelHelper {

	public JClass typeMirrorToJClass(TypeMirror type, EBeanHolder holder) {

		if (type instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) type;

			String declaredTypeName = declaredType.asElement().toString();

			JClass declaredClass = holder.refClass(declaredTypeName);

			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

			List<JClass> typeArgumentJClasses = new ArrayList<JClass>();
			for (TypeMirror typeArgument : typeArguments) {
				typeArgumentJClasses.add(typeMirrorToJClass(typeArgument, holder));
			}
			if (typeArgumentJClasses.size() > 0) {
				declaredClass = declaredClass.narrow(typeArgumentJClasses);
			}

			return declaredClass;
		} else if (type instanceof WildcardType) {
			// TODO : At his time (01/2013), it is not possible to handle the
			// super bound because code model does not offer a way to model
			// statement like " ? super X"
			// (see http://java.net/jira/browse/CODEMODEL-11)
			WildcardType wildcardType = (WildcardType) type;

			TypeMirror extendsBound = wildcardType.getExtendsBound();

			return typeMirrorToJClass(extendsBound, holder).wildcard();
		} else if (type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) type;

			JClass refClass = typeMirrorToJClass(arrayType.getComponentType(), holder);

			return refClass.array();
		} else {
			return holder.refClass(type.toString());
		}
	}

	public static class Parameter {
		public final String name;
		public final JClass jClass;

		public Parameter(String name, JClass jClass) {
			this.name = name;
			this.jClass = jClass;
		}
	}

	public JMethod overrideAnnotatedMethod(ExecutableElement executableElement, EBeanHolder holder) {

		String methodName = executableElement.getSimpleName().toString();

		JClass returnType = typeMirrorToJClass(executableElement.getReturnType(), holder);

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			parameters.add(new Parameter(parameterName, parameterClass));
		}

		JMethod existingMethod = findAlreadyGeneratedMethod(holder.generatedClass, methodName, parameters);

		if (existingMethod != null) {
			return existingMethod;
		}

		JMethod method = holder.generatedClass.method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		List<JVar> methodParameters = new ArrayList<JVar>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			JClass parameterClass = typeMirrorToJClass(parameter.asType(), holder);
			JVar param = method.param(JMod.FINAL, parameterClass, parameterName);
			methodParameters.add(param);
		}

		for (TypeMirror superThrownType : executableElement.getThrownTypes()) {
			JClass thrownType = typeMirrorToJClass(superThrownType, holder);
			method._throws(thrownType);
		}

		callSuperMethod(method, holder, method.body());

		return method;
	}

	private JMethod findAlreadyGeneratedMethod(JDefinedClass definedClass, String methodName, List<Parameter> parameters) {
		method: for (JMethod method : definedClass.methods()) {
			if (method.name().equals(methodName) && method.params().size() == parameters.size()) {
				int i = 0;
				for (JVar param : method.params()) {
					String searchedParamType = parameters.get(i).jClass.name();
					if (!param.type().name().equals(searchedParamType)) {
						continue method;
					}
					i++;
				}
				return method;
			}
		}
		return null;
	}

	public void callSuperMethod(JMethod superMethod, EBeanHolder holder, JBlock callBlock) {
		JExpression activitySuper = holder.generatedClass.staticRef("super");
		JInvocation superCall = JExpr.invoke(activitySuper, superMethod);

		for (JVar param : superMethod.params()) {
			superCall.arg(param);
		}

		JType returnType = superMethod.type();
		if (returnType.fullName().equals("void")) {
			callBlock.add(superCall);
		} else {
			callBlock._return(superCall);
		}
	}

	public JBlock removeBody(JMethod method) {
		JBlock body = method.body();
		try {
			Field bodyField = JMethod.class.getDeclaredField("body");
			bodyField.setAccessible(true);
			bodyField.set(method, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		JBlock clonedBody = new JBlock(false, false);

		for (Object statement : body.getContents()) {
			clonedBody.add((JStatement) statement);
		}

		return clonedBody;
	}

	public String getIdStringFromIdFieldRef(JFieldRef idRef) {
		try {
			Field nameField = JFieldRef.class.getDeclaredField("name");
			nameField.setAccessible(true);
			String name = (String) nameField.get(idRef);

			if (name != null) {
				return name;
			}

			Field varField = JFieldRef.class.getDeclaredField("var");
			varField.setAccessible(true);
			JVar var = (JVar) varField.get(idRef);

			if (var != null) {
				return var.name();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		throw new IllegalStateException("Unable to extract target name from JFieldRef");
	}

	public JDefinedClass createDelegatingAnonymousRunnableClass(EBeanHolder holder, JMethod delegatedMethod) {

		JCodeModel codeModel = holder.codeModel();

		JDefinedClass anonymousRunnableClass;
		JBlock previousMethodBody = removeBody(delegatedMethod);

		anonymousRunnableClass = codeModel.anonymousClass(Runnable.class);

		JMethod runMethod = anonymousRunnableClass.method(JMod.PUBLIC, codeModel.VOID, "run");
		runMethod.annotate(Override.class);

		runMethod.body().add(previousMethodBody);

		return anonymousRunnableClass;
	}

	public JVar castContextToActivity(EBeanHolder holder, JBlock ifActivityBody) {
		JClass activityClass = holder.classes().ACTIVITY;
		return ifActivityBody.decl(activityClass, "activity", cast(activityClass, holder.contextRef));
	}

	public JBlock ifContextInstanceOfActivity(EBeanHolder holder, JBlock methodBody) {
		return methodBody._if(holder.contextRef._instanceof(holder.classes().ACTIVITY))._then();
	}

	public void copyConstructorsAndAddStaticEViewBuilders(Element element, JCodeModel codeModel, JClass eBeanClass, EBeanHolder holder, JMethod setContentViewMethod, JMethod init) {
		List<ExecutableElement> constructors = new ArrayList<ExecutableElement>();
		for (Element e : element.getEnclosedElements()) {
			if (e.getKind() == CONSTRUCTOR) {
				constructors.add((ExecutableElement) e);
			}
		}

		for (ExecutableElement userConstructor : constructors) {
			JMethod copyConstructor = holder.generatedClass.constructor(PUBLIC);
			JMethod staticHelper = holder.generatedClass.method(PUBLIC | STATIC, eBeanClass, "build");
			JBlock body = copyConstructor.body();
			JInvocation superCall = body.invoke("super");
			JInvocation newInvocation = JExpr._new(holder.generatedClass);
			for (VariableElement param : userConstructor.getParameters()) {
				String paramName = param.getSimpleName().toString();
				String paramType = param.asType().toString();
				copyConstructor.param(holder.refClass(paramType), paramName);
				staticHelper.param(holder.refClass(paramType), paramName);
				superCall.arg(JExpr.ref(paramName));
				newInvocation.arg(JExpr.ref(paramName));
			}

			JVar newCall = staticHelper.body().decl(holder.generatedClass, "instance", newInvocation);
			staticHelper.body().invoke(newCall, "onFinishInflate");
			staticHelper.body()._return(newCall);
			body.invoke(init);
		}
	}

	public JVar findParameterByName(JMethod method, String name) {
		for (JVar parameter : method.params()) {
			if (parameter.name().equals(name)) {
				return parameter;
			}
		}
		return null;
	}

	public void addActivityIntentBuilder(JCodeModel codeModel, EBeanHolder holder, AnnotationHelper annotationHelper) throws Exception {
		addIntentBuilder(codeModel, holder, annotationHelper, true);
	}

	public void addServiceIntentBuilder(JCodeModel codeModel, EBeanHolder holder, AnnotationHelper annotationHelper) throws Exception {
		addIntentBuilder(codeModel, holder, annotationHelper, false);
	}

	private void addIntentBuilder(JCodeModel codeModel, EBeanHolder holder, AnnotationHelper annotationHelper, boolean isActivity) throws JClassAlreadyExistsException {
		JClass contextClass = holder.classes().CONTEXT;
		JClass intentClass = holder.classes().INTENT;
		JClass fragmentClass = holder.classes().FRAGMENT;
		JClass fragmentSupportClass = holder.classes().SUPPORT_V4_FRAGMENT;

		{
			holder.intentBuilderClass = holder.generatedClass._class(PUBLIC | STATIC, "IntentBuilder_");

			JFieldVar contextField = holder.intentBuilderClass.field(PRIVATE, contextClass, "context_");

			holder.intentField = holder.intentBuilderClass.field(PRIVATE | FINAL, intentClass, "intent_");
			{
				// Constructor
				JMethod constructor = holder.intentBuilderClass.constructor(JMod.PUBLIC);
				JVar constructorContextParam = constructor.param(contextClass, "context");
				JBlock constructorBody = constructor.body();
				constructorBody.assign(contextField, constructorContextParam);
				constructorBody.assign(holder.intentField, _new(intentClass).arg(constructorContextParam).arg(holder.generatedClass.dotclass()));
			}
			// Additional constructor for fragments (issue #541)
			Elements elementUtils = annotationHelper.getElementUtils();
			boolean fragmentInClasspath = elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT) != null;
			boolean fragmentSupportInClasspath = elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT) != null;

			JFieldVar fragmentField = null;
			if (fragmentInClasspath) {
				fragmentField = addIntentBuilderFragmentConstructor(holder, fragmentClass, "fragment_", contextField);
			}
			JFieldVar fragmentSupportField = null;
			if (fragmentSupportInClasspath) {
				fragmentSupportField = addIntentBuilderFragmentConstructor(holder, fragmentSupportClass, "fragmentSupport_", contextField);
			}

			{
				// get()
				JMethod method = holder.intentBuilderClass.method(PUBLIC, intentClass, "get");
				method.body()._return(holder.intentField);
			}

			{
				// flags()
				JMethod method = holder.intentBuilderClass.method(PUBLIC, holder.intentBuilderClass, "flags");
				JVar flagsParam = method.param(codeModel.INT, "flags");
				JBlock body = method.body();
				body.invoke(holder.intentField, "setFlags").arg(flagsParam);
				body._return(_this());
			}

			if (isActivity) {
				// start()
				JMethod method = holder.intentBuilderClass.method(PUBLIC, codeModel.VOID, "start");
				method.body().invoke(contextField, "startActivity").arg(holder.intentField);

				// startForResult()
				method = holder.intentBuilderClass.method(PUBLIC, codeModel.VOID, "startForResult");
				JVar requestCode = method.param(codeModel.INT, "requestCode");

				JBlock body = method.body();
				JClass activityClass = holder.classes().ACTIVITY;

				JConditional condition = null;
				if (fragmentSupportField != null) {
					condition = body._if(fragmentSupportField.ne(JExpr._null()));
					condition._then() //
							.invoke(fragmentSupportField, "startActivityForResult").arg(holder.intentField).arg(requestCode);
				}
				if (fragmentField != null) {
					if (condition == null) {
						condition = body._if(fragmentField.ne(JExpr._null()));
					} else {
						condition = condition._elseif(fragmentField.ne(JExpr._null()));
					}
					condition._then() //
							.invoke(fragmentField, "startActivityForResult").arg(holder.intentField).arg(requestCode);
				}
				if (condition == null) {
					condition = body._if(contextField._instanceof(activityClass));
				} else {
					condition = condition._elseif(contextField._instanceof(activityClass));
				}
				condition._then() //
						.invoke(JExpr.cast(activityClass, contextField), "startActivityForResult").arg(holder.intentField).arg(requestCode);
				condition._else() //
						.invoke(contextField, "startActivity").arg(holder.intentField);
			} else {
				// start()
				JMethod method = holder.intentBuilderClass.method(PUBLIC, holder.classes().COMPONENT_NAME, "start");
				method.body()._return(contextField.invoke("startService").arg(holder.intentField));

				// stop()
				method = holder.intentBuilderClass.method(PUBLIC, codeModel.BOOLEAN, "stop");
				method.body()._return(contextField.invoke("stopService").arg(holder.intentField));
			}

			{
				// intent() with activity param
				JMethod method = holder.generatedClass.method(STATIC | PUBLIC, holder.intentBuilderClass, "intent");
				JVar contextParam = method.param(contextClass, "context");
				method.body()._return(_new(holder.intentBuilderClass).arg(contextParam));
			}
			if (fragmentInClasspath) {
				// intent() with android.app.Fragment param
				JMethod method = holder.generatedClass.method(STATIC | PUBLIC, holder.intentBuilderClass, "intent");
				JVar fragmentParam = method.param(fragmentClass, "fragment");
				method.body()._return(_new(holder.intentBuilderClass).arg(fragmentParam));
			}
			if (fragmentSupportInClasspath) {
				// intent() with android.support.v4.app.Fragment param
				JMethod method = holder.generatedClass.method(STATIC | PUBLIC, holder.intentBuilderClass, "intent");
				JVar fragmentParam = method.param(fragmentSupportClass, "fragment");
				method.body()._return(_new(holder.intentBuilderClass).arg(fragmentParam));
			}
		}
	}

	public JInvocation addIntentBuilderPutExtraMethod(JCodeModel codeModel, EBeanHolder holder, APTCodeModelHelper helper, ProcessingEnvironment processingEnv, JMethod method, TypeMirror elementType, String parameterName, JFieldVar extraKeyField) {
		boolean castToSerializable = false;
		boolean castToParcelable = false;
		if (elementType.getKind() == TypeKind.DECLARED) {
			Elements elementUtils = processingEnv.getElementUtils();
			Types typeUtils = processingEnv.getTypeUtils();
			TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
			if (!typeUtils.isSubtype(elementType, parcelableType)) {
				TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
				if (!typeUtils.isSubtype(elementType, stringType)) {
					castToSerializable = true;
				}
			} else {
				TypeMirror serializableType = elementUtils.getTypeElement(SERIALIZABLE).asType();
				if (typeUtils.isSubtype(elementType, serializableType)) {
					castToParcelable = true;
				}
			}
		}

		JClass parameterClass = helper.typeMirrorToJClass(elementType, holder);
		JVar extraParameterVar = method.param(parameterClass, parameterName);
		JBlock body = method.body();
		JInvocation invocation = body.invoke(holder.intentField, "putExtra").arg(extraKeyField);
		if (castToSerializable) {
			return invocation.arg(cast(holder.classes().SERIALIZABLE, extraParameterVar));
		} else if (castToParcelable) {
			return invocation.arg(cast(holder.classes().PARCELABLE, extraParameterVar));
		}
		return invocation.arg(extraParameterVar);
	}

	public void addCastMethod(JCodeModel codeModel, EBeanHolder holder) {
		JType objectType = codeModel._ref(Object.class);
		JMethod method = holder.generatedClass.method(JMod.PRIVATE, objectType, "cast_");
		JTypeVar genericType = method.generify("T");
		method.type(genericType);
		JVar objectParam = method.param(objectType, "object");
		method.annotate(SuppressWarnings.class).param("value", "unchecked");
		method.body()._return(JExpr.cast(genericType, objectParam));
		holder.cast = method;
	}

	private JFieldVar addIntentBuilderFragmentConstructor(EBeanHolder holder, JClass fragmentClass, String fieldName, JFieldVar contextField) {

		JFieldVar fragmentField = holder.intentBuilderClass.field(PRIVATE, fragmentClass, fieldName);
		JMethod constructor = holder.intentBuilderClass.constructor(JMod.PUBLIC);
		JVar constructorFragmentParam = constructor.param(fragmentClass, "fragment");
		JBlock constructorBody = constructor.body();
		constructorBody.assign(fragmentField, constructorFragmentParam);
		constructorBody.assign(contextField, constructorFragmentParam.invoke("getActivity"));
		constructorBody.assign(holder.intentField, _new(holder.classes().INTENT).arg(contextField).arg(holder.generatedClass.dotclass()));
		return fragmentField;
	}
}
