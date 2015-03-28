package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptMultipleAssignment {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testParseNormalVarAssign() {
		String script = "var yy = 1;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var yy = 1", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseDoubleVarAssign() {
		String script = "var yy = var zz= 1;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var yy = var zz = 1", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseDoubleVarAssign2() {
		String script = "var yy = zz= 1;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var yy = zz = 1", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseTripleVarAssign() {
		String script = "var yy = var zz= qq = 1;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var yy = var zz = qq = 1", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseDoubleVarAssign3() {
		String script = "yy = zz= 1;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("yy = zz = 1", scriptStatement.Decompile(0));
	}
}
