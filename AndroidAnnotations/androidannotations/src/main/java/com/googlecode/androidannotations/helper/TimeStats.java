/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class TimeStats {

	private final Map<String, Long> measures = new HashMap<String, Long>();
	private final Map<String, Long> durations = new HashMap<String, Long>();
	
	private Messager messager;

	public void start(String key) {
		long start = System.currentTimeMillis();
		measures.put(key, start);
	}

	public void stop(String key) {
		Long start = measures.remove(key);
		if (start != null) {
			long end = System.currentTimeMillis();
			Long duration = end - start;
			durations.put(key, duration);
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Time measurements: ");
		for (Entry<String, Long> entry : durations.entrySet()) {
			sb.append("[") //
					.append(entry.getKey()) //
					.append(" = ") //
					.append(entry.getValue()) //
					.append(" ms], ");
		}

		return sb.toString();
	}
	
	public void logStats() {
		if (messager != null) {
			messager.printMessage(Diagnostic.Kind.NOTE, toString());
		}
	}

	public void setMessager(Messager messager) {
		this.messager = messager;
	}

	public void clear() {
		measures.clear();
		durations.clear();
	}

}
