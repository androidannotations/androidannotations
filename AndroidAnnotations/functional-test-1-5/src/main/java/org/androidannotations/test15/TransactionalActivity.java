/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.test15.instancestate.MySerializableBean;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

@EActivity
public class TransactionalActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Transactional
	void successfulTransaction(SQLiteDatabase db) {
		db.execSQL("Some SQL");
	}

	@Transactional
	void rollbackedTransaction(SQLiteDatabase db) {
		throw new IllegalArgumentException();
	}

	@Transactional
	MySerializableBean transactionnalWithReturnStatement(SQLiteDatabase db) {
		return null;
	}

	@Transactional
	void mehodUsingArrayParameters(SQLiteDatabase db, //
			MySerializableBean[] parameters) {
		// do some stuff here
	}

	@Transactional
	void mehodUsingParametrizedParameters(SQLiteDatabase db, //
			List<MySerializableBean> parameters) {
		// do some stuff here
	}

	@Transactional
	@Background
	void backgroundTransactionalAnnotatedMethod(SQLiteDatabase db, //
			List<MySerializableBean[]> parameters) {
		// do some stuff here
	}

}
