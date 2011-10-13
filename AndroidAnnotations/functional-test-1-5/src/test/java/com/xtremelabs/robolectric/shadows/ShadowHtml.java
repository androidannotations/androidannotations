package com.xtremelabs.robolectric.shadows;

import android.text.Html;
import android.text.Spanned;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(Html.class)
public class ShadowHtml {
	@Implementation
	public static Spanned fromHtml(String source) {
		return new SpannedThatActsLikeString(source);
	}

	private static class SpannedThatActsLikeString implements Spanned {
		String source;

		private SpannedThatActsLikeString(String source) {
			this.source = source;
		}

		@Override
		public <T> T[] getSpans(int start, int end, Class<T> type) {
			return null;
		}

		@Override
		public int getSpanStart(Object tag) {
			return 0;
		}

		@Override
		public int getSpanEnd(Object tag) {
			return 0;
		}

		@Override
		public int getSpanFlags(Object tag) {
			return 0;
		}

		@Override
		public int nextSpanTransition(int start, int limit, Class type) {
			return 0;
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public char charAt(int i) {
			return 0;
		}

		@Override
		public CharSequence subSequence(int i, int i1) {
			return null;
		}

		@Override
		public String toString() {
			return source;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SpannedThatActsLikeString)
				return source.equals(((SpannedThatActsLikeString) o).source);
			else
				return source.equals(o);
		}

		@Override
		public int hashCode() {
			return source != null ? source.hashCode() : 0;
		}
	}
}