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

import org.junit.Assert;
import org.junit.Test;

public class LoggerTest {

	@Test
	public void testIsLoggable() throws Exception {
		LoggerContext loggerContext = LoggerContext.getInstance();
		loggerContext.setCurrentLevel(Level.INFO);
		Logger logger = new Logger(loggerContext, getClass().getName());

		Assert.assertFalse(logger.isLoggable(Level.DEBUG));
		Assert.assertTrue(logger.isLoggable(Level.INFO));
		Assert.assertTrue(logger.isLoggable(Level.WARN));
	}

}
