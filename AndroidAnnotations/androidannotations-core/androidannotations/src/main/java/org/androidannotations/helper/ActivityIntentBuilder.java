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
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ActivityIntentBuilder extends IntentBuilder {

	private static final int MIN_SDK_WITH_FRAGMENT_SUPPORT = 11;

	private static final int MIN_SDK_WITH_ACTIVITY_OPTIONS = 16;

	private JFieldVar fragmentField;
	private JFieldVar fragmentSupportField;

	private JFieldRef optionsField;

	public ActivityIntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		super(holder, androidManifest);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();

		optionsField = ref("lastOptions");

		createAdditionalConstructor(); // See issue #541
		createAdditionalIntentMethods();
		overrideStartForResultMethod();
	}

	private void createAdditionalIntentMethods() {
		if (hasFragmentInClasspath()) {
			// intent() with android.app.Fragment param
			JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().FRAGMENT, "fragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
		if (hasFragmentSupportInClasspath()) {
			// intent() with android.support.v4.app.Fragment param
			JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().SUPPORT_V4_FRAGMENT, "supportFragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
	}

	@Override
	protected JClass getSuperClass() {
		JClass superClass = holder.refClass(org.androidannotations.api.builder.ActivityIntentBuilder.class);
		return superClass.narrow(builderClass);
	}

	private void createAdditionalConstructor() {
		if (hasFragmentInClasspath()) {
			fragmentField = addFragmentConstructor(holder.classes().FRAGMENT, "fragment" + generationSuffix());
		}
		if (hasFragmentSupportInClasspath()) {
			fragmentSupportField = addFragmentConstructor(holder.classes().SUPPORT_V4_FRAGMENT, "fragmentSupport" + generationSuffix());
		}
	}

	private JFieldVar addFragmentConstructor(JClass fragmentClass, String fieldName) {
		JFieldVar fragmentField = holder.getIntentBuilderClass().field(PRIVATE, fragmentClass, fieldName);
		JExpression generatedClass = holder.getGeneratedClass().dotclass();

		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorFragmentParam = constructor.param(fragmentClass, "fragment");
		JBlock constructorBody = constructor.body();
		constructorBody.invoke("super").arg(constructorFragmentParam.invoke("getActivity")).arg(generatedClass);
		constructorBody.assign(fragmentField, constructorFragmentParam);

		return fragmentField;
	}

	private void overrideStartForResultMethod() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "startForResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(holder.codeModel().INT, "requestCode");
		JBlock body = method.body();

		JConditional condition = null;
		if (fragmentSupportField != null) {
			condition = body._if(fragmentSupportField.ne(JExpr._null()));
			condition._then() //
					.invoke(fragmentSupportField, "startActivityForResult").arg(intentField).arg(requestCode);
		}
		if (fragmentField != null) {
			if (condition == null) {
				condition = body._if(fragmentField.ne(JExpr._null()));
			} else {
				condition = condition._elseif(fragmentField.ne(JExpr._null()));
			}

			JBlock fragmentStartForResultInvocationBlock;

			if (hasActivityOptionsInFragment() && shouldGuardActivityOptions()) {
				fragmentStartForResultInvocationBlock = createCallWithIfGuard(requestCode, condition._then(), fragmentField);
			} else {
				fragmentStartForResultInvocationBlock = condition._then();
			}
			JInvocation invocation = fragmentStartForResultInvocationBlock //
					.invoke(fragmentField, "startActivityForResult").arg(intentField).arg(requestCode);
			if (hasActivityOptionsInFragment()) {
				invocation.arg(optionsField);
			}
		}

		JBlock activityStartInvocationBlock = null;

		if (condition != null) {
			activityStartInvocationBlock = condition._else();
		} else {
			activityStartInvocationBlock = method.body();
		}

		JConditional activityCondition = activityStartInvocationBlock._if(contextField._instanceof(holder.classes().ACTIVITY));
		JBlock thenBlock = activityCondition._then();
		JVar activityVar = thenBlock.decl(holder.classes().ACTIVITY, "activity", JExpr.cast(holder.classes().ACTIVITY, contextField));

		if (hasActivityCompatInClasspath() && hasActivityOptionsInActivityCompat()) {
			thenBlock.staticInvoke(holder.classes().ACTIVITY_COMPAT, "startActivityForResult") //
					.arg(activityVar).arg(intentField).arg(requestCode).arg(optionsField);
		} else if (hasActivityOptionsInFragment()) {
			JBlock startForResultInvocationBlock;
			if (shouldGuardActivityOptions()) {
				startForResultInvocationBlock = createCallWithIfGuard(requestCode, thenBlock, activityVar);
			} else {
				startForResultInvocationBlock = thenBlock;
			}

			startForResultInvocationBlock.invoke(activityVar, "startActivityForResult") //
				.arg(intentField).arg(requestCode).arg(optionsField);
		} else {
			thenBlock.invoke(activityVar, "startActivityForResult").arg(intentField).arg(requestCode);
		}

		if (hasActivityOptionsInFragment()) {
			JBlock startInvocationBlock;
			if (shouldGuardActivityOptions()) {
				startInvocationBlock = createCallWithIfGuard(null, activityCondition._else(), contextField);
			} else {
				startInvocationBlock = activityCondition._else();
			}
			startInvocationBlock.invoke(contextField, "startActivity").arg(intentField).arg(optionsField);
		} else {
			activityCondition._else().invoke(contextField, "startActivity").arg(intentField);
		}
	}

	private JBlock createCallWithIfGuard(JVar requestCode, JBlock thenBlock, JExpression invocationTarget) {
		JConditional guardIf = thenBlock._if(holder.classes().BUILD_VERSION.staticRef("SDK_INT").gte(holder.classes().BUILD_VERSION_CODES.staticRef("JELLY_BEAN")));
		JBlock startInvocationBlock = guardIf._then();
		String methodName = requestCode != null ? "startActivityForResult" : "startActivity";

		JInvocation invocation = guardIf._else().invoke(invocationTarget, methodName).arg(intentField);
		if (requestCode != null) {
			invocation.arg(requestCode);
		}
		return startInvocationBlock;
	}

	protected boolean hasFragmentInClasspath() {
		boolean fragmentExistsInSdk = androidManifest.getMinSdkVersion() >= MIN_SDK_WITH_FRAGMENT_SUPPORT;
		return fragmentExistsInSdk && elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT) != null;
	}

	protected boolean hasFragmentSupportInClasspath() {
		return elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT) != null;
	}

	protected boolean hasActivityCompatInClasspath() {
		return elementUtils.getTypeElement(CanonicalNameConstants.ACTIVITY_COMPAT) != null;
	}

	protected boolean hasActivityOptionsInFragment() {
		if (!hasFragmentInClasspath()) {
			return false;
		}

		TypeElement fragment = elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT);

		return hasActivityOptions(fragment, 1);
	}

	protected boolean hasActivityOptionsInActivityCompat() {
		TypeElement activityCompat = elementUtils.getTypeElement(CanonicalNameConstants.ACTIVITY_COMPAT);

		return hasActivityOptions(activityCompat, 2);
	}

	private boolean hasActivityOptions(TypeElement type, int optionsParamPosition) {
		if (type == null) {
			return false;
		}

		for (ExecutableElement element : ElementFilter.methodsIn(elementUtils.getAllMembers(type))) {
			if (element.getSimpleName().contentEquals("startActivity")) {
				List<? extends VariableElement> parameters = element.getParameters();
				if (parameters.size() == optionsParamPosition + 1) {
					VariableElement parameter = parameters.get(optionsParamPosition);
					if (parameter.asType().toString().equals(CanonicalNameConstants.BUNDLE)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean shouldGuardActivityOptions() {
		return androidManifest.getMinSdkVersion() < MIN_SDK_WITH_ACTIVITY_OPTIONS;
	}
}
