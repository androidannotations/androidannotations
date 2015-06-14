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

import java.io.Serializable;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.accounts.Account;
import android.app.Fragment;

@EFragment
public class GenericFragmentArguments<S extends Serializable, P extends Account> extends Fragment {

	@FragmentArg
	S[] serializableArray;

	@FragmentArg
	P[] parcelableArray;

	@FragmentArg
	S serializable;

	@FragmentArg
	P parcelable;

}