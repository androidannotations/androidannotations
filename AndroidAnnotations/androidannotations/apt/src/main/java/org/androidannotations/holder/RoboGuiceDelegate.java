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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class RoboGuiceDelegate extends GeneratedClassHolderDelegate<EActivityHolder> {

	// TODO access for these fields should be refactored

	protected JFieldVar scopedObjects;
	protected JFieldVar scope;
	protected JFieldVar eventManager;
	public JFieldVar contentViewListenerField;
	protected JBlock onRestartBeforeSuperBlock;
	protected JBlock onRestartAfterSuperBlock;
	protected JBlock onStartBeforeSuperBlock;
	protected JBlock onResumeBeforeSuperBlock;
	protected JBlock onPauseAfterSuperBlock;
	protected JBlock onNewIntentAfterSuperBlock;
	protected JMethod onStop;
	protected JMethod onDestroy;
	protected JVar newConfig;
	protected JVar currentConfig;
	protected JBlock onConfigurationChangedAfterSuperBlock;
	protected JBlock onContentChangedAfterSuperBlock;

	public RoboGuiceDelegate(EActivityHolder holder) {
		super(holder);
	}

	public JFieldVar getEventManagerField() {
		if (eventManager == null) {
			holder.setEventManagerField();
		}
		return eventManager;
	}

	public JFieldVar getScopedObjectsField() {
		if (scopedObjects == null) {
			holder.setScopedObjectsField();
		}
		return scopedObjects;
	}

	public JFieldVar getScopeField() {
		if (scope == null) {
			holder.setScopeField();
		}
		return scope;
	}

	public JFieldVar getContentViewListenerField() {
		if (contentViewListenerField == null) {
			holder.setContentViewListenerField();
		}
		return contentViewListenerField;
	}

	public JBlock getOnRestartBeforeSuperBlock() {
		if (onRestartBeforeSuperBlock == null) {
			holder.setOnRestart();
		}
		return onRestartBeforeSuperBlock;
	}

	public JBlock getOnRestartAfterSuperBlock() {
		if (onRestartAfterSuperBlock == null) {
			holder.setOnRestart();
		}
		return onRestartAfterSuperBlock;
	}

	public JBlock getOnStartBeforeSuperBlock() {
		if (onStartBeforeSuperBlock == null) {
			holder.setOnStart();
		}
		return onStartBeforeSuperBlock;
	}

	public JBlock getOnStartAfterSuperBlock() {
		return holder.getOnStartAfterSuperBlock();
	}

	public JBlock getOnResumeBeforeSuperBlock() {
		if (onResumeBeforeSuperBlock == null) {
			holder.setOnResume();
		}
		return onResumeBeforeSuperBlock;
	}

	public JBlock getOnResumeAfterSuperBlock() {
		return holder.getOnResumeAfterSuperBlock();
	}

	public JBlock getOnPauseAfterSuperBlock() {
		if (onPauseAfterSuperBlock == null) {
			holder.setOnPause();
		}
		return onPauseAfterSuperBlock;
	}

	public JBlock getOnNewIntentAfterSuperBlock() {
		if (onNewIntentAfterSuperBlock == null) {
			holder.setOnNewIntent();
		}
		return onNewIntentAfterSuperBlock;
	}

	public JMethod getOnStop() {
		if (onStop == null) {
			holder.setOnStop();
		}
		return onStop;
	}

	public JMethod getOnDestroy() {
		if (onDestroy == null) {
			holder.setOnDestroy();
		}
		return onDestroy;
	}

	public JVar getNewConfig() {
		if (newConfig == null) {
			holder.setOnConfigurationChanged();
		}
		return newConfig;
	}

	public JVar getCurrentConfig() {
		if (currentConfig == null) {
			holder.setOnConfigurationChanged();
		}
		return currentConfig;
	}

	public JBlock getOnConfigurationChangedAfterSuperBlock() {
		if (onConfigurationChangedAfterSuperBlock == null) {
			holder.setOnConfigurationChanged();
		}
		return onConfigurationChangedAfterSuperBlock;
	}

	public JBlock getOnContentChangedAfterSuperBlock() {
		if (onContentChangedAfterSuperBlock == null) {
			holder.setOnContentChanged();
		}
		return onContentChangedAfterSuperBlock;
	}

	public JBlock getOnActivityResultAfterSuperBlock() {
		return holder.getOnActivityResultAfterSuperBlock();
	}

	public JVar getRequestCode() {
		return holder.getOnActivityResultRequestCodeParam();
	}

	public JVar getResultCode() {
		return holder.getOnActivityResultResultCodeParam();
	}

	public JVar getData() {
		return holder.getOnActivityResultDataParam();
	}
}
