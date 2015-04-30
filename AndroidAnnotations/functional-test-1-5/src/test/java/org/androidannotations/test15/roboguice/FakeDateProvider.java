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
package org.androidannotations.test15.roboguice;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class FakeDateProvider implements Provider<Date> {
	private Date date = new Date();

	@Override
	public Date get() {
		return date;
	}

	public void setDate(String dateString) {
		try {
			date = DateFormat.getDateInstance(DateFormat.LONG, Locale.US).parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("bad date!!");
		}
	}
}
