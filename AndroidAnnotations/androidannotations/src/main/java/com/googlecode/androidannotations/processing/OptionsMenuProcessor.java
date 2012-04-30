/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.helper.SherlockHelper;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class OptionsMenuProcessor implements ElementProcessor {

	private final IRClass rClass;

	public OptionsMenuProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OptionsMenu.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getRelativeEBeanHolder(element);

		boolean usesSherlock = new SherlockHelper().usesSherlock(holder);
		
		OptionsMenu layoutAnnotation = element.getAnnotation(OptionsMenu.class);
		int layoutIdValue = layoutAnnotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.MENU);
		JFieldRef optionsMenuId = rInnerClass.getIdStaticRef(layoutIdValue, holder);

		JMethod method = holder.eBean.method(PUBLIC, codeModel.BOOLEAN, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JVar menuParam = method.param(holder.refClass(usesSherlock? "com.actionbarsherlock.view.Menu": "android.view.Menu"), "menu");

		JBlock body = method.body();

		JVar menuInflater = body.decl(holder.refClass(usesSherlock? "com.actionbarsherlock.view.MenuInflator": "android.view.MenuInflater"), "menuInflater", invoke(usesSherlock? "getSupportMenuInflater": "getMenuInflater"));

		body.invoke(menuInflater, "inflate").arg(optionsMenuId).arg(menuParam);

		body._return(invoke(_super(), method).arg(menuParam));
	}

}
