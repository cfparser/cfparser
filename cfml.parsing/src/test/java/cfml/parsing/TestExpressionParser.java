package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.utils.TestUtils;

public class TestExpressionParser {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testHangs() {
		String script = "gm.setChangedBy(arguments.user.getFirstName() & \" \" & arguments.user.getMiddleInitial() & \" \" & arguments.user.getLastName() & \" [\" & arguments.user.getUserName() & \"] [\" & arguments.user.getEmail() & \"]\")";
		// String script = "arguments.user.getFirstName() & \" \"";
		// String script = "\" \" & \" \"";
		CFExpression scriptStatement = TestUtils.parseExpression(script);
		assertNotNull(scriptStatement);
		assertEquals(
				"gm.setChangedBy(arguments.user.getFirstName() & ' ' & arguments.user.getMiddleInitial() & ' ' & arguments.user.getLastName() & ' [' & arguments.user.getUserName() & '] [' & arguments.user.getEmail() & ']')",
				scriptStatement.Decompile(0));
	}
	
	@Test
	public void testScope() {
		String script = "this.param.datasource.napAS400 = arguments.datasource";
		// String script = "x={name:\"attributes\",xx:-1}";
		CFExpression scriptStatement = TestUtils.parseExpression(script);
		assertNotNull(scriptStatement);
		assertEquals("this.param.datasource.napAS400=arguments.datasource", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testNestedHash() {
		String script = "x = #CGI.y#";
		// String script = "x={name:\"attributes\",xx:-1}";
		CFExpression scriptStatement = TestUtils.parseExpression(script);
		TestUtils.printCFExpressionTree(scriptStatement, "");
		assertNotNull(scriptStatement);
		assertEquals("x=#CGI.y#", scriptStatement.Decompile(0));
	}
	
}
