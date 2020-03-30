/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.helper;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.EViewHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ViewNotifierHelper {

	private AndroidAnnotationsEnvironment environment;
	private EComponentWithViewSupportHolder holder;
	private JFieldVar notifier;

	public ViewNotifierHelper(EComponentWithViewSupportHolder holder, AndroidAnnotationsEnvironment environment) {
		this.holder = holder;
		this.environment = environment;
	}

	public void invokeViewChanged(JBlock block) {
		block.invoke(notifier, "notifyViewChanged").arg(_this());
	}

	public JVar replacePreviousNotifier(JBlock block) {
		AbstractJClass notifierClass = holder.getEnvironment().getJClass(OnViewChangedNotifier.class);
		if (notifier == null) {
			notifier = holder.getGeneratedClass().field(PRIVATE | FINAL, notifierClass, "onViewChangedNotifier" + generationSuffix(), _new(notifierClass));
			implementHasViewsInHolder();
		}
		return block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(notifier));
	}

	private void implementHasViewsInHolder() {
		holder.getGeneratedClass()._implements(HasViews.class);
		JCodeModel codeModel = environment.getCodeModel();

		JDirectClass genericType = codeModel.directClass("T");
		JMethod findViewById = holder.getGeneratedClass().method(PUBLIC, genericType, "internalFindViewById");
		findViewById.generify("T", environment.getClasses().VIEW);
		findViewById.annotate(Override.class);

		JVar idParam = findViewById.param(codeModel.INT, "id");
		IJExpression findViewByIdExpression = holder.getFindViewByIdExpression(idParam);
		findViewById.body()._return(JExpr.cast(genericType, findViewByIdExpression));
	}

	public JVar replacePreviousNotifierWithNull(JBlock block) {
		AbstractJClass notifierClass = holder.getEnvironment().getJClass(OnViewChangedNotifier.class);
		return block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(_null()));
	}

	public void resetPreviousNotifier(JBlock block, JVar previousNotifier) {
		AbstractJClass notifierClass = holder.getEnvironment().getJClass(OnViewChangedNotifier.class);
		block.staticInvoke(notifierClass, "replaceNotifier").arg(previousNotifier);
	}

	public void wrapInitWithNotifier() {
		JBlock initBlock = holder.getInit().body();
		JVar previousNotifier = replacePreviousNotifier(initBlock);
		((EViewHolder) holder).setInitBody(holder.getInit().body().blockSimple());
		resetPreviousNotifier(initBlock.blockSimple(), previousNotifier);
	}

}
