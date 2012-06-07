/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package greendroid.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * This is a fake GreenDroid activity that has the same signature. Used to test
 * AndroidAnnotations integration with GreenDroid.
 */
public class GDActivity extends Activity {

	public void setActionBarContentView(int layoutResID) {
	}

	public void setActionBarContentView(View view) {
	}

	public void setActionBarContentView(View view, LayoutParams params) {
	}

}
