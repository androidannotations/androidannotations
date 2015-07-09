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

import static com.sun.codemodel.JMod.PUBLIC;

import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;

public class EViewGroupHolder extends EViewHolder {

	private JBlock setContentViewBlock;

	public EViewGroupHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	protected void setOnFinishInflate() {
		onFinishInflate = generatedClass.method(PUBLIC, codeModel().VOID, "onFinishInflate");
		onFinishInflate.annotate(Override.class);
		onFinishInflate.javadoc().append(ALREADY_INFLATED_COMMENT);

		JBlock ifNotInflated = onFinishInflate.body()._if(getAlreadyInflated().not())._then();
		ifNotInflated.assign(getAlreadyInflated(), JExpr.TRUE);

		setContentViewBlock = ifNotInflated.block();

		getInit();
		viewNotifierHelper.invokeViewChanged(ifNotInflated);

		onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");
	}

	public JBlock getSetContentViewBlock() {
		if (setContentViewBlock == null) {
			setOnFinishInflate();
		}
		return setContentViewBlock;
	}
}
