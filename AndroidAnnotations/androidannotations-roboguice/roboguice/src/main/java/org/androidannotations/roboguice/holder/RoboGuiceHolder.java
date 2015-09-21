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
package org.androidannotations.roboguice.holder;

import static org.androidannotations.helper.ModelConstants.generationSuffix;

import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.plugin.PluginClassHolder;
import org.androidannotations.roboguice.helper.RoboGuiceClasses;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class RoboGuiceHolder extends PluginClassHolder<EActivityHolder> {

	private static final String ON_CONTENT_CHANGED_JAVADOC = "We cannot simply copy the " + "code from RoboActivity, because that can cause classpath issues. "
			+ "For further details see issue #1116.";

	protected JFieldVar scopedObjects;
	protected JFieldVar scope;
	protected JFieldVar eventManager;
	public JFieldVar contentViewListenerField;
	protected JVar currentConfig;
	protected JBlock onContentChangedAfterSuperBlock;

	public RoboGuiceHolder(EActivityHolder holder) {
		super(holder);
	}

	public JFieldVar getEventManagerField() {
		if (eventManager == null) {
			eventManager = getGeneratedClass().field(JMod.PROTECTED, getJClass(RoboGuiceClasses.EVENT_MANAGER), "eventManager" + generationSuffix());
		}
		return eventManager;
	}

	public JFieldVar getScopedObjectsField() {
		if (scopedObjects == null) {
			AbstractJClass keyWildCard = getJClass(RoboGuiceClasses.KEY).narrow(getCodeModel().wildcard());
			AbstractJClass scopedHashMap = environment().getClasses().HASH_MAP.narrow(keyWildCard, environment().getClasses().OBJECT);
			scopedObjects = getGeneratedClass().field(JMod.PROTECTED, scopedHashMap, "scopedObjects" + generationSuffix());
			scopedObjects.assign(JExpr._new(scopedHashMap));
		}
		return scopedObjects;
	}

	public JFieldVar getScopeField() {
		if (scope == null) {
			scope = getGeneratedClass().field(JMod.PRIVATE, getJClass(RoboGuiceClasses.CONTEXT_SCOPE), "scope" + generationSuffix());
		}
		return scope;
	}

	public JFieldVar getContentViewListenerField() {
		if (contentViewListenerField == null) {
			contentViewListenerField = getGeneratedClass().field(JMod.NONE, getJClass(RoboGuiceClasses.CONTENT_VIEW_LISTENER), "ignored" + generationSuffix());
			contentViewListenerField.annotate(getJClass(RoboGuiceClasses.INJECT));
		}
		return contentViewListenerField;
	}

	public JBlock getOnContentChangedAfterSuperBlock() {
		if (onContentChangedAfterSuperBlock == null) {
			holder().getOnContentChanged().javadoc().append(ON_CONTENT_CHANGED_JAVADOC);
			onContentChangedAfterSuperBlock = holder().getOnContentChangedAfterSuperBlock();
		}
		return onContentChangedAfterSuperBlock;
	}

	public JVar getCurrentConfig() {
		if (currentConfig == null) {
			AbstractJClass configurationClass = environment().getClasses().CONFIGURATION;
			JBlock onConfigurationChangedBeforeSuperBlock = holder().getOnConfigurationChangedBeforeSuperBlock();
			currentConfig = onConfigurationChangedBeforeSuperBlock.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));
			onConfigurationChangedBeforeSuperBlock.bracesRequired(false).indentRequired(false);
		}
		return currentConfig;
	}
}
