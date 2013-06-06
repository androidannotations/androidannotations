package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.holder.OnSeekBarChangeListenerHolder;

import javax.annotation.processing.ProcessingEnvironment;

public class SeekBarTouchStopHandler extends AbstractSeekBarTouchHandler {

	public SeekBarTouchStopHandler(ProcessingEnvironment processingEnvironment) {
		super(SeekBarTouchStop.class, processingEnvironment);
	}

	@Override
	protected JBlock getMethodBodyToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder) {
		return onSeekBarChangeListenerHolder.getOnStopTrackingTouchBody();
	}

	@Override
	protected JVar getMethodParamToPass(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder) {
		return onSeekBarChangeListenerHolder.getOnStopTrackingTouchSeekBarParam();
	}
}
