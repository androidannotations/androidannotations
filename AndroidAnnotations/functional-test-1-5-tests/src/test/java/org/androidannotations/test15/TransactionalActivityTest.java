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
package org.androidannotations.test15;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import android.database.sqlite.SQLiteDatabase;

@RunWith(AndroidAnnotationsTestRunner.class)
public class TransactionalActivityTest {

	private SQLiteDatabase mockDb;
	private TransactionalActivity_ activity;

	@Before
	public void setup() {
		activity = new TransactionalActivity_();
		activity.onCreate(null);
		mockDb = mock(SQLiteDatabase.class);
	}

	@Test
	public void successfulTransaction() {
		activity.successfulTransaction(mockDb);

		InOrder inOrder = inOrder(mockDb);

		inOrder.verify(mockDb).beginTransaction();
		inOrder.verify(mockDb).execSQL(anyString());
		inOrder.verify(mockDb).setTransactionSuccessful();
		inOrder.verify(mockDb).endTransaction();
	}

	@Test
	public void rollbackedTransaction() {

		try {
			activity.rollbackedTransaction(mockDb);
			fail("This method should throw an exception");
		} catch (IllegalArgumentException e) {
		}

		verify(mockDb, never()).setTransactionSuccessful();

		InOrder inOrder = inOrder(mockDb);

		inOrder.verify(mockDb).beginTransaction();
		inOrder.verify(mockDb).endTransaction();
	}

}
