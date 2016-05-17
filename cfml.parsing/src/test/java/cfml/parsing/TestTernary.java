package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestTernary {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testParseScriptTernaryFunction() {
		String script = "someVariable = someExpression ? someExpression2 : someExpression3;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("someVariable = someExpression?someExpression2:someExpression3", scriptStatement.Decompile(0));
	}
	
}
