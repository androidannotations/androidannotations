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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

@EActivity(R.layout.textviews_injected)
public class EditorActionsHandledActivity extends Activity {

	boolean actionHandled = false;

	int actionId;

	KeyEvent keyEvent;

	@ViewById
	EditText editText1;

	EditText passedEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@EditorAction(R.id.editText1)
	void editorActionWithFullParameters(TextView tv, int actionId, KeyEvent event) {
		actionHandled = true;
	}

	@EditorAction(R.id.editText4)
	void editorActionWithEditTextParameter(EditText et) {
		passedEditText = et;
	}

	@EditorAction(R.id.editText2)
	void editorActionInversedParameters(KeyEvent event, int actionId) {
		this.actionId = actionId;
	}

	@EditorAction(R.id.editText3)
	void editorActionOtherInversedParameters(KeyEvent event, TextView textView) {
		keyEvent = event;
	}

	@EditorAction(R.id.textView1)
	void editorActionNoParameters() {

	}

	@EditorAction(R.id.textView2)
	boolean editorActionWithBooleanReturned(int actionId, TextView tv) {
		return false;
	}
}
