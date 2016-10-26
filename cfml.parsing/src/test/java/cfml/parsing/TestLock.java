package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestLock {
	
	@Test
	public void testLock() {
		String script = "lock name=\"fw1_#application.applicationName#_#variables.framework.applicationKey#_#type#_#componentKey#\" type=\"exclusive\" timeout=\"30\" {}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		System.out.println(scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
		assertNotNull(scriptStatement);
		assertEquals(
				"lock  name='fw1_#application.applicationName#_#variables.framework.applicationKey#_#type#_#componentKey#' timeout='30' type='exclusive'{}",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
				
	}
}
