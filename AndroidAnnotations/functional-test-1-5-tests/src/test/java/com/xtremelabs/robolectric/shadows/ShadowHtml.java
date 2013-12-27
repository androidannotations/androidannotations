/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

		@SuppressWarnings("rawtypes")
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
