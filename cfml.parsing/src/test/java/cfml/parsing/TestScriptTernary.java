package cfml.parsing;

import static cfml.parsing.utils.TestUtils.assertTreeNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.reporting.ParseException;
import cfml.parsing.utils.TestUtils;

public class TestScriptTernary {
	
	private CFMLParser fCfmlParser;
	private static final String sourceUrlFile = "file:src/test/resources/cfml/test1.cfm";
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testParseScriptParens() throws ParseException, IOException {
		String script = "5 * (1 + 2);";
		CFScriptStatement scriptStatement = fCfmlParser.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("5 * (1 + 2)", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseScriptTernaryFunction() throws ParseException, IOException {
		String script = "if(((x EQ 1) OR true) AND (true OR true)){} ";
		CFScriptStatement scriptStatement = fCfmlParser.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("if(((x EQ 1) OR true) AND (true OR true) )   {  }",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testParseLocalAssignment() {
		String script = "var x = 123;";
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptBlockContext(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "var", "x", "=", "123", ";", "<EOF>");
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testAssignment() {
		String script = "x = 123;";
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptBlockContext(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "x", "=", "123", ";", "<EOF>");
		assertNotNull(scriptStatement);
	}
}
