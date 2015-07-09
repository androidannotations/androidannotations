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
package org.androidannotations.logger;

public enum Level {
	TRACE(1, "TRACE"), //
	DEBUG(2, "DEBUG"), //
	INFO(3, "INFO "), //
	WARN(4, "WARN "), //
	ERROR(5, "ERROR");

	public final int weight;
	public final String name;

	private Level(int weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	public boolean isGreaterOrEquals(Level l) {
		return weight >= l.weight;
	}

	public boolean isSmaller(Level l) {
		return weight < l.weight;
	}

	public static Level parse(String name) {
		for (Level level : values()) {
			if (level.name().equalsIgnoreCase(name)) {
				return level;
			}
		}
		throw new IllegalArgumentException("Can't find Level matching " + name);
	}

}
