package org.androidannotations.logger;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FormatterTest {

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
	public void testBuildFullMessage_string() throws Exception {
		Assert.assertEquals("This is a test", Formatter.buildFullMessage("{} is a test", "This"));
		Assert.assertEquals("This is a test", Formatter.buildFullMessage("This is a {}", "test"));
		Assert.assertEquals("This is a test", Formatter.buildFullMessage("This {} {} test", "is", "a"));
		Assert.assertEquals("This is a test", Formatter.buildFullMessage("This {} a test", "is", "a"));
	}

	@Test
	public void testBuildFullMessage_int_array() throws Exception {
		Integer[] values = new Integer[] { 1, 2 };
		Assert.assertEquals("Values = [1, 2]", Formatter.buildFullMessage("Values = {}", new Object[] { values }));
	}

	@Test
	public void testBuildFullMessage_object_array() throws Exception {
		SomeObject[] values = new SomeObject[] { new SomeObject("a"), new SomeObject("b") };
		Assert.assertEquals("Objects = [a, b]", Formatter.buildFullMessage("Objects = {}", new Object[] { values }));
	}

	@Test
	public void testBuildFullMessage_list() throws Exception {
		List<SomeObject> values = Arrays.asList(new SomeObject("a"), new SomeObject("b"));
		Assert.assertEquals("Objects = [a, b]", Formatter.buildFullMessage("Objects = {}", values));
	}

}
