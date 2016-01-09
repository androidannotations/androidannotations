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
package org.androidannotations.internal.helper;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.EViewHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;

public class ViewNotifierHelper {

	private EComponentHolder holder;
	private JFieldVar notifier;

	public ViewNotifierHelper(EComponentHolder holder) {
		this.holder = holder;
	}

	private JFieldVar getNotifier() {
		if (notifier == null) {
			AbstractJClass notifierClass = holder.getEnvironment().getJClass(OnViewChangedNotifier.class);
			notifier = holder.getGeneratedClass().field(PRIVATE | FINAL, notifierClass, "onViewChangedNotifier" + generationSuffix(), _new(notifierClass));
			holder.getGeneratedClass()._implements(HasViews.class);
		}
		return notifier;
	}

	public void invokeViewChanged(JBlock block) {
		block.invoke(getNotifier(), "notifyViewChanged").arg(_this());
	}

	public JVar replacePreviousNotifier(JBlock block) {
		AbstractJClass notifierClass = holder.getEnvironment().getJClass(OnViewChangedNotifier.class);
		return block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(getNotifier()));
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
		JBlock initBlock = holder.getInitBodyInjectionBlock();
		JVar previousNotifier = replacePreviousNotifier(initBlock);
		JBlock block = holder.getInit().body().blockSimple();
		((EViewHolder) holder).setInitBody(block);
		resetPreviousNotifier(block, previousNotifier);
	}

}
