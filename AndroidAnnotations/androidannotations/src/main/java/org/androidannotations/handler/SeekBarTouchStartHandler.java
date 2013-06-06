package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.SeekBarTouchStart;
import org.androidannotations.holder.OnSeekBarChangeListenerHolder;

import javax.annotation.processing.ProcessingEnvironment;

public class SeekBarTouchStartHandler extends AbstractSeekBarTouchHandler {

	public SeekBarTouchStartHandler(ProcessingEnvironment processingEnvironment) {
		super(SeekBarTouchStart.class, processingEnvironment);
	}

	@Override
	protected JBlock getMethodBodyToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder) {
		return onSeekBarChangeListenerHolder.getOnStartTrackingTouchBody();
	}

	@Override
	protected JVar getMethodParamToPass(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder) {
		return onSeekBarChangeListenerHolder.getOnStartTrackingTouchSeekBarParam();
	}
}
