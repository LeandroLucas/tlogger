/**
 * 
 */
package com.lebittec.tlogger.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 *
 */
public class TLoggerTest {

	@Before
	public void before() {
		TLogger.setup("token-here", -0l);
	}

	@Test
	@Ignore
	public void loggerTest() {
		TLogger.getLogger().send("testando tlogger");
	}

}
