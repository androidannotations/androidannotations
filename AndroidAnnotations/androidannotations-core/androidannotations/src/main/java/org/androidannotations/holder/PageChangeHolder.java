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
package org.androidannotations.holder;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPrimitiveType;
import com.helger.jcodemodel.JVar;

public class PageChangeHolder {

	private EComponentWithViewSupportHolder holder;
	private JVar viewPagerVariable;
	private JDefinedClass listenerClass;
	private JBlock pageScrollStateChangedBody;
	private JVar pageScrollStateChangedStateParam;
	private JBlock pageScrolledBody;
	private JVar pageScrolledPositionParam;
	private JVar pageScrolledPositionOffsetParam;
	private JVar pageScrolledPositionOffsetPixelsParam;
	private JBlock pageSelectedBody;
	private JVar pageSelectedPositionParam;

	public PageChangeHolder(EComponentWithViewSupportHolder holder, JVar viewPagerVariable, JDefinedClass onPageChangeListenerClass) {
		this.holder = holder;
		this.viewPagerVariable = viewPagerVariable;
		listenerClass = onPageChangeListenerClass;
		createPageScrollStateChanged();
		createPageScroll();
		createPageSelected();
	}

	private void createPageScrollStateChanged() {
		JMethod method = listenerClass.method(JMod.PUBLIC, holder.getCodeModel().VOID, "onPageScrollStateChanged");
		method.annotate(Override.class);
		pageScrollStateChangedBody = method.body();
		JPrimitiveType intClass = holder.getCodeModel().INT;
		pageScrollStateChangedStateParam = method.param(intClass, "state");
	}

	private void createPageScroll() {
		JMethod method = listenerClass.method(JMod.PUBLIC, holder.getCodeModel().VOID, "onPageScrolled");
		method.annotate(Override.class);
		pageScrolledBody = method.body();
		JPrimitiveType intClass = holder.getCodeModel().INT;
		pageScrolledPositionParam = method.param(intClass, "position");
		pageScrolledPositionOffsetParam = method.param(holder.getCodeModel().FLOAT, "positionOffset");
		pageScrolledPositionOffsetPixelsParam = method.param(intClass, "positionOffsetPixels");
	}

	private void createPageSelected() {
		JMethod method = listenerClass.method(JMod.PUBLIC, holder.getCodeModel().VOID, "onPageSelected");
		method.annotate(Override.class);
		pageSelectedBody = method.body();
		JPrimitiveType intClass = holder.getCodeModel().INT;
		pageSelectedPositionParam = method.param(intClass, "position");
	}

	public JVar getViewPagerVariable() {
		return viewPagerVariable;
	}

	public JDefinedClass getListenerClass() {
		return listenerClass;
	}

	public JBlock getPageScrollStateChangedBody() {
		return pageScrollStateChangedBody;
	}

	public JVar getPageScrollStateChangedStateParam() {
		return pageScrollStateChangedStateParam;
	}

	public JBlock getPageScrolledBody() {
		return pageScrolledBody;
	}

	public JVar getPageScrolledPositionParam() {
		return pageScrolledPositionParam;
	}

	public JVar getPageScrolledPositionOffsetParam() {
		return pageScrolledPositionOffsetParam;
	}

	public JVar getPageScrolledPositionOffsetPixelsParam() {
		return pageScrolledPositionOffsetPixelsParam;
	}

	public JBlock getPageSelectedBody() {
		return pageSelectedBody;
	}

	public JVar getPageSelectedPositionParam() {
		return pageSelectedPositionParam;
	}
}
