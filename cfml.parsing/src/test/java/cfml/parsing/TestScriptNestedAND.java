package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptNestedAND {
	
	private CFMLParser fCfmlParser;
	private static final String sourceUrlFile = "file:src/test/resources/cfml/test1.cfm";
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testParseScriptTernaryFunction() {
		String script = "result = fileExists(destfile) ? \"overwritten\" : \"created\";";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("result=fileExists(destfile)?'overwritten':'created'", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseScriptTernaryFunctionParen() {
		String script = "result = (fileExists(destfile)) ? \"overwritten\" : \"created\";";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("result=(fileExists(destfile))?'overwritten':'created'", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseScriptTernaryString() {
		// String script = "result = (fileExists(destfile)) ? \"overwritten\" : \"created\";";
		String script = "result = a == b ? \"overwritten\" : \"created\";";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("result=a==b?'overwritten':'created'", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseScriptTernaryChain() {
		String script = "result = a == b ? c > a ? 'c > a' : 'a > c' : 'b != a';";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("result=a==b?c>a?'c > a':'a > c':'b != a'", scriptStatement.Decompile(0));
	}
	
}
