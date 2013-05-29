package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.process.ProcessHolder;

public class RoboGuiceHolder {

	private EActivityHolder holder;
	protected JFieldVar scope;
	protected JFieldVar eventManager;
	protected JMethod getInjector;
	protected JBlock onRestartBeforeSuperBlock;
	protected JBlock onRestartAfterSuperBlock;
	protected JBlock onStartBeforeSuperBlock;
	protected JBlock onStartAfterSuperBlock;
	protected JBlock onResumeBeforeSuperBlock;
	protected JBlock onResumeAfterSuperBlock;
	protected JBlock onPauseAfterSuperBlock;
	protected JBlock onNewIntentAfterSuperBlock;
	protected JBlock onStopBeforeSuperBlock;
	protected JBlock onDestroyBeforeSuperBlock;
	protected JVar newConfig;
	protected JVar currentConfig;
	protected JBlock onConfigurationChangedAfterSuperBlock;
	protected JBlock onContentChangedAfterSuperBlock;
	protected JBlock onActivityResultAfterSuperBlock;
	protected JVar requestCode;
	protected JVar resultCode;
	protected JVar data;

	public RoboGuiceHolder(EActivityHolder holder) {
		this.holder = holder;
	}

	public ProcessHolder.Classes classes() {
		return holder.classes();
	}

	public JFieldVar getEventManagerField() {
		if (eventManager == null) {
			holder.setEventManagerField();
		}
		return eventManager;
	}

	public JFieldVar getScopeField() {
		if (scope == null) {
			holder.setScopeField();
		}
		return scope;
	}

	public JMethod getGetInjector() {
		if (getInjector == null) {
			holder.setGetInjector();
		}
		return getInjector;
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
		if (onStartAfterSuperBlock == null) {
			holder.setOnStart();
		}
		return onStartAfterSuperBlock;
	}

	public JBlock getOnResumeBeforeSuperBlock() {
		if (onResumeBeforeSuperBlock == null) {
			holder.setOnResume();
		}
		return onResumeBeforeSuperBlock;
	}

	public JBlock getOnResumeAfterSuperBlock() {
		if (onResumeAfterSuperBlock == null) {
			holder.setOnResume();
		}
		return onResumeAfterSuperBlock;
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

	public JBlock getOnStopBeforeSuperBlock() {
		if (onStopBeforeSuperBlock == null) {
			holder.setOnStop();
		}
		return onStopBeforeSuperBlock;
	}

	public JBlock getOnDestroyBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			holder.setOnDestroy();
		}
		return onDestroyBeforeSuperBlock;
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
		if (onActivityResultAfterSuperBlock == null) {
			holder.setOnActivityResult();
		}
		return onActivityResultAfterSuperBlock;
	}

	public JVar getRequestCode() {
		if (requestCode == null) {
			holder.setOnActivityResult();
		}
		return requestCode;
	}

	public JVar getResultCode() {
		if (resultCode == null) {
			holder.setOnActivityResult();
		}
		return resultCode;
	}

	public JVar getData() {
		if (data == null) {
			holder.setOnActivityResult();
		}
		return data;
	}
}
