package org.androidannotations.holder;

import com.sun.codemodel.*;

public class OnSeekBarChangeListenerHolder {

	private EComponentWithViewSupportHolder holder;
	private JDefinedClass listenerClass;
	private JBlock onProgressChangedBody;
	private JVar onProgressChangedSeekBarParam;
	private JVar onProgressChangedProgressParam;
	private JVar onProgressChangedFromUserParam;
	private JBlock onStartTrackingTouchBody;
	private JVar onStartTrackingTouchSeekBarParam;
	private JBlock onStopTrackingTouchBody;
	private JVar onStopTrackingTouchSeekBarParam;

	public OnSeekBarChangeListenerHolder(EComponentWithViewSupportHolder holder, JDefinedClass onSeekbarChangeListenerClass) {
		this.holder = holder;
		this.listenerClass = onSeekbarChangeListenerClass;
		createOnProgressChanged();
		createOnStartTrackingTouch();
		createOnStopTrackingTouch();
	}

	private void createOnProgressChanged() {
		JMethod onProgressChangedMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onProgressChanged");
		onProgressChangedMethod.annotate(Override.class);
		onProgressChangedBody = onProgressChangedMethod.body();
		onProgressChangedSeekBarParam = onProgressChangedMethod.param(holder.classes().SEEKBAR, "seekBar");
		onProgressChangedProgressParam = onProgressChangedMethod.param(holder.codeModel().INT, "progress");
		onProgressChangedFromUserParam = onProgressChangedMethod.param(holder.codeModel().BOOLEAN, "fromUser");
	}

	private void createOnStartTrackingTouch() {
		JMethod onStartTrackingTouchMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onStartTrackingTouch");
		onStartTrackingTouchMethod.annotate(Override.class);
		onStartTrackingTouchBody = onStartTrackingTouchMethod.body();
		onStartTrackingTouchSeekBarParam = onStartTrackingTouchMethod.param(holder.classes().SEEKBAR, "seekBar");
	}

	private void createOnStopTrackingTouch() {
		JMethod onStopTrackingTouchMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onStopTrackingTouch");
		onStopTrackingTouchMethod.annotate(Override.class);
		onStopTrackingTouchBody = onStopTrackingTouchMethod.body();
		onStopTrackingTouchSeekBarParam = onStopTrackingTouchMethod.param(holder.classes().SEEKBAR, "seekBar");
	}

	public JBlock getOnProgressChangedBody() {
		return onProgressChangedBody;
	}

	public JVar getOnProgressChangedSeekBarParam() {
		return onProgressChangedSeekBarParam;
	}

	public JVar getOnProgressChangedProgressParam() {
		return onProgressChangedProgressParam;
	}

	public JVar getOnProgressChangedFromUserParam() {
		return onProgressChangedFromUserParam;
	}

	public JBlock getOnStartTrackingTouchBody() {
		return onStartTrackingTouchBody;
	}

	public JVar getOnStartTrackingTouchSeekBarParam() {
		return onStartTrackingTouchSeekBarParam;
	}

	public JBlock getOnStopTrackingTouchBody() {
		return onStopTrackingTouchBody;
	}

	public JVar getOnStopTrackingTouchSeekBarParam() {
		return onStopTrackingTouchSeekBarParam;
	}
}
