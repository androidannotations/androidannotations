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
package org.androidannotations.logger.formatter;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FormatterTest {

	private Formatter formatter = new FormatterSimple();

	class SomeObject {
		String name;

		public SomeObject(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Test
	public void testBuildFullMessageString() throws Exception {
		Assert.assertEquals("This is a test", formatter.buildFullMessage("{} is a test", "This"));
		Assert.assertEquals("This is a test", formatter.buildFullMessage("This is a {}", "test"));
		Assert.assertEquals("This is a test", formatter.buildFullMessage("This {} {} test", "is", "a"));
		Assert.assertEquals("This is a test", formatter.buildFullMessage("This {} a test", "is", "a"));
	}

	@Test
	public void testBuildFullMessageIntArray() throws Exception {
		Integer[] values = new Integer[] { 1, 2 };
		Assert.assertEquals("Values = [1, 2]", formatter.buildFullMessage("Values = {}", new Object[] { values }));
	}

	@Test
	public void testBuildFullMessageObjectArray() throws Exception {
		SomeObject[] values = new SomeObject[] { new SomeObject("a"), new SomeObject("b") };
		Assert.assertEquals("Objects = [a, b]", formatter.buildFullMessage("Objects = {}", new Object[] { values }));
	}

	@Test
	public void testBuildFullMessageList() throws Exception {
		List<SomeObject> values = Arrays.asList(new SomeObject("a"), new SomeObject("b"));
		Assert.assertEquals("Objects = [a, b]", formatter.buildFullMessage("Objects = {}", values));
	}

}
