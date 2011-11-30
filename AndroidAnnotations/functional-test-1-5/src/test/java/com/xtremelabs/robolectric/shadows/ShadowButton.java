package com.xtremelabs.robolectric.shadows;

import android.view.View;
import android.widget.Button;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(Button.class)
public class ShadowButton extends ShadowTextView {

	private View.OnLongClickListener onLongClickListener;

	public ShadowButton() {
		System.out.println();
	}

	@Implementation
	public boolean performLongClick() {
		if (onLongClickListener != null) {
			onLongClickListener.onLongClick(realView);
			return true;
		}
		return false;
	}

	@Implementation
	public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
		this.onLongClickListener = onLongClickListener;
	}

}
