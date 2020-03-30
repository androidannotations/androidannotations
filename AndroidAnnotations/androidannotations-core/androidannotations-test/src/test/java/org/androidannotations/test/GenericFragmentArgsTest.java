/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.accounts.Account;
import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class GenericFragmentArgsTest {

	private static final Account[] TEST_ARRAY = new Account[] { new Account("Android", "Annotations") };

	@Test
	public void testParcelableArrayFragmentArgInjected() {
		Bundle bundle = new Bundle();
		bundle.putParcelableArray("parcelableArray", TEST_ARRAY);

		GenericFragmentArguments<CloseableSerializable, Account> fragment = new GenericFragmentArguments_<CloseableSerializable, Account>();
		fragment.setArguments(bundle);

		assertThat(fragment.parcelableArray).isNull();

		fragment.onCreate(null);

		assertThat(fragment.parcelableArray).isEqualTo(TEST_ARRAY);
	}

	private static class CloseableSerializable implements Closeable, Serializable {
		@Override
		public void close() throws IOException {

		}
	}
}
