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
package android.support.v4.app;

import android.app.Activity;
import android.content.Intent;

/**
 * We have to put this on resources folder because we want to add it to
 * classpath only on some unit tests methods
 */
public class Fragment {

	public void onCreate(android.os.Bundle savedInstanceState) {

	}

	public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {

	}

	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
		return null;
	}

	public void onDestroyView() {

	}

	public void setArguments(android.os.Bundle args) {

	}

	public Activity getActivity() {
		return null;
	}

	public void startActivityForResult(Intent intent, int flag) {

	}

	public FragmentManager getFragmentManager() {
		return null;
	}

	public FragmentManager getChildFragmentManager() {
		return null;
	}

	public abstract class FragmentManager {

		public abstract Fragment findFragmentById(int id);

		public abstract Fragment findFragmentByTag(String tag);
	}

}
