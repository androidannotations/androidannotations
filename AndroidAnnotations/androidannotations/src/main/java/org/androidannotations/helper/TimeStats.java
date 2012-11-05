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
package org.androidannotations.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class TimeStats {

	private final Map<String, Long> measures = new HashMap<String, Long>();
	private final List<Duration> durations = new ArrayList<Duration>();

	private static class Duration implements Comparable<Duration> {
		public final String key;
		public final long durationInMs;

		public Duration(String key, long durationInMs) {
			this.key = key;
			this.durationInMs = durationInMs;
		}

		@Override
		public int compareTo(Duration o) {
			return (int) (o.durationInMs - durationInMs);
		}
	}

	private Messager messager;

	public void start(String key) {
		long start = System.currentTimeMillis();
		measures.put(key, start);
	}

	public void stop(String key) {
		Long start = measures.remove(key);
		if (start != null) {
			long end = System.currentTimeMillis();
			long duration = end - start;
			durations.add(new Duration(key, duration));
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Time measurements: ");

		Collections.sort(durations);
		for (Duration duration : durations) {
			sb.append("[") //
					.append(duration.key) //
					.append(" = ") //
					.append(duration.durationInMs) //
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
