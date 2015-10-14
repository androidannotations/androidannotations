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

import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public abstract class EComponentHolder extends BaseGeneratedClassHolder {

	protected IJExpression contextRef;
	protected JMethod init;
	private JBlock initBodyInjectionBlock;
	private JBlock initBodyAfterInjectionBlock;
	private JVar resourcesRef;
	private JFieldVar powerManagerRef;

	public EComponentHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
	}

	public IJExpression getContextRef() {
		if (contextRef == null) {
			setContextRef();
		}
		return contextRef;
	}

	protected abstract void setContextRef();

	public JMethod getInit() {
		if (init == null) {
			setInit();
		}
		return init;
	}

	protected abstract void setInit();

	public JBlock getInitBody() {
		return getInit().body();
	}

	public JBlock getInitBodyInjectionBlock() {
		if (initBodyInjectionBlock == null) {
			setInitBodyBlocks();
		}

		return initBodyInjectionBlock;
	}

	public JBlock getInitBodyAfterInjectionBlock() {
		if (initBodyAfterInjectionBlock == null) {
			setInitBodyBlocks();
		}

		return initBodyAfterInjectionBlock;
	}

	private void setInitBodyBlocks() {
		initBodyInjectionBlock = getInitBody().blockVirtual();
		initBodyAfterInjectionBlock = getInitBody().blockVirtual();
	}

	public JVar getResourcesRef() {
		if (resourcesRef == null) {
			setResourcesRef();
		}
		return resourcesRef;
	}

	private void setResourcesRef() {
		resourcesRef = getInitBodyInjectionBlock().decl(getClasses().RESOURCES, "resources" + generationSuffix(), getContextRef().invoke("getResources"));
	}

	public JFieldVar getPowerManagerRef() {
		if (powerManagerRef == null) {
			setPowerManagerRef();
		}

		return powerManagerRef;
	}

	private void setPowerManagerRef() {
		JBlock methodBody = getInitBodyInjectionBlock();

		JFieldRef serviceRef = getClasses().CONTEXT.staticRef("POWER_SERVICE");
		powerManagerRef = getGeneratedClass().field(PRIVATE, getClasses().POWER_MANAGER, "powerManager" + generationSuffix());
		methodBody.assign(powerManagerRef, cast(getClasses().POWER_MANAGER, getContextRef().invoke("getSystemService").arg(serviceRef)));
	}
}
