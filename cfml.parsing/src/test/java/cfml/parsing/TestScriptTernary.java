package cfml.parsing;

import static cfml.parsing.utils.TestUtils.assertTreeNodes;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTParser;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.parsing.utils.TestUtils;

public class TestScriptTernary {
	
	private CFMLParser fCfmlParser;
	private static final String sourceUrlFile = "file:src/test/resources/cfml/test1.cfm";
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void testParseScriptParens() {
		String script = "5 * (1 + 2);";
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptBlockContext(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "if", "(", "(", "(", "x", "EQ", "1", ")", "OR", "true", ")", "AND", "(", "true",
				"OR", "true", ")", ")", "{", "}", "<EOF>");
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptTernaryFunction() {
		String script = "if(((x EQ 1) OR true) AND (true OR true)){} ";
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptBlockContext(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "if", "(", "(", "(", "x", "EQ", "1", ")", "OR", "true", ")", "AND", "(", "true",
				"OR", "true", ")", ")", "{", "}", "<EOF>");
		assertNotNull(scriptStatement);
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
		TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
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
		TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "x", "=", "123", ";", "<EOF>");
		assertNotNull(scriptStatement);
	}
}
