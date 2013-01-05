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
package org.androidannotations.test15.efragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.test15.R;

import android.app.Activity;


@EActivity(R.layout.fragments)
public class MyFragmentActivity extends Activity {

	@FragmentById
	public MyFragment myFragment;
	
	@FragmentById(R.id.myFragment)
	public MyFragment myFragment2;

	@FragmentByTag
	public MyFragment myFragmentTag;
	
	@FragmentByTag("myFragmentTag")
	public MyFragment myFragmentTag2;
	
	@Bean
	public BeanWithFragments beanWithFragments;

}
