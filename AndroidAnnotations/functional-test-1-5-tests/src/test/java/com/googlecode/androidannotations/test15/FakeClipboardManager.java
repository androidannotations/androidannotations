package com.googlecode.androidannotations.test15;

import android.text.ClipboardManager;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

public class FakeClipboardManager extends ClipboardManager {

	private CharSequence text;

	@Override
	public CharSequence getText() {
		return text;
	}

	@Override
	public void setText(CharSequence text) {
		this.text = text;
	}

	@Override
	public boolean hasText() {
		return text != null;
	}
}
