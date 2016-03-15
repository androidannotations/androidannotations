/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
import static com.helger.jcodemodel.JExpr.ref;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasIntentBuilder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

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
			JVar fragmentParam = method.param(getClasses().FRAGMENT, "fragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
		if (hasFragmentSupportInClasspath()) {
			// intent() with android.support.v4.app.Fragment param
			JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(getClasses().SUPPORT_V4_FRAGMENT, "supportFragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
	}

	@Override
	protected AbstractJClass getSuperClass() {
		AbstractJClass superClass = environment.getJClass(org.androidannotations.api.builder.ActivityIntentBuilder.class);
		return superClass.narrow(builderClass);
	}

	private void createAdditionalConstructor() {
		if (hasFragmentInClasspath()) {
			fragmentField = addFragmentConstructor(getClasses().FRAGMENT, "fragment" + generationSuffix());
		}
		if (hasFragmentSupportInClasspath()) {
			fragmentSupportField = addFragmentConstructor(getClasses().SUPPORT_V4_FRAGMENT, "fragmentSupport" + generationSuffix());
		}
	}

	private JFieldVar addFragmentConstructor(AbstractJClass fragmentClass, String fieldName) {
		JFieldVar fragmentField = holder.getIntentBuilderClass().field(PRIVATE, fragmentClass, fieldName);
		IJExpression generatedClass = holder.getGeneratedClass().dotclass();

		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorFragmentParam = constructor.param(fragmentClass, "fragment");
		JBlock constructorBody = constructor.body();
		constructorBody.invoke("super").arg(constructorFragmentParam.invoke("getActivity")).arg(generatedClass);
		constructorBody.assign(fragmentField, constructorFragmentParam);

		return fragmentField;
	}

	private void overrideStartForResultMethod() {
		AbstractJClass postActivityStarterClass = environment.getJClass(PostActivityStarter.class);

		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, postActivityStarterClass, "startForResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(environment.getCodeModel().INT, "requestCode");
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

		JConditional activityCondition = activityStartInvocationBlock._if(contextField._instanceof(getClasses().ACTIVITY));
		JBlock thenBlock = activityCondition._then();
		JVar activityVar = thenBlock.decl(getClasses().ACTIVITY, "activity", JExpr.cast(getClasses().ACTIVITY, contextField));

		if (hasActivityCompatInClasspath() && hasActivityOptionsInActivityCompat()) {
			thenBlock.staticInvoke(getClasses().ACTIVITY_COMPAT, "startActivityForResult") //
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

		body._return(_new(postActivityStarterClass).arg(contextField));
	}

	private JBlock createCallWithIfGuard(JVar requestCode, JBlock thenBlock, IJExpression invocationTarget) {
		JConditional guardIf = thenBlock._if(getClasses().BUILD_VERSION.staticRef("SDK_INT").gte(getClasses().BUILD_VERSION_CODES.staticRef("JELLY_BEAN")));
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
