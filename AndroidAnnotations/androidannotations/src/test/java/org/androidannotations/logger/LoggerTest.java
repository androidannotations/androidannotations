package org.androidannotations.logger;

import junit.framework.Assert;

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
