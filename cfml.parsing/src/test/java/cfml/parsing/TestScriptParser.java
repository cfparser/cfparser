package cfml.parsing;

import static cfml.parsing.utils.TestUtils.assertTreeNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFLiteral;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptParser {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	private ScriptBlockContext parseScript(String script) {
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptBlockContext(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		return scriptStatement;
	}
	
	@Test
	public void testParseScript() {
		String script = "var x = 1; y = 5; createObject('java','java.lang.String');";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "var", "x", "=", "1", ";", "y", "=", "5", ";", "createObject", "(", "'java'", ",",
				"'java.lang.String'", ")", ";", "<EOF>");
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptMissingSemiColon() {
		String script = "var x = 1; y = 5 createObject('java','java.lang.String');";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() == 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		System.out.println(fCfmlParser.printMessages());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptMissingAssignment() {
		String script = "var x = 1; y =; createObject('java','java.lang.String');";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() == 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testForIn() {
		String script = "for(widget in thingWithWidgets.getWidgets()) { writeOutput(widget); }";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "for", "(", "widget", "in", "thingWithWidgets", ".", "getWidgets", "(", "", ")",
				")", "{", "writeOutput", "(", "widget", ")", ";", "}", "<EOF>");
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testNewOperator() {
		String script = "datatypes = new CFDataTypes().package();";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		List<ParseTree> nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "datatypes", "=", "new", "CFDataTypes", "(", "", ")", ".", "package", "(", "", ")",
				";", "<EOF>");
		
		script = "datatypes = new CFDataTypes().package().member;";
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		nodesList = TestUtils.getLeaves(scriptStatement);
		assertTreeNodes(nodesList, "datatypes", "=", "new", "CFDataTypes", "(", "", ")", ".", "package", "(", "", ")",
				".", "member", ";", "<EOF>");
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testFuncNameMatchesAccessType() {
		String script = "function package() {}";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testAccessTypeAndFuncNameMatch() {
		String script = "package function package() {}";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptBadLex() {
		String script = "string function fart(required kind, area='elavator') {"
				+ "var toot = 'se5ee6yye67tutuityit69t9imfuihki';"
				+ "var registry = createObject('java','org.eclipse.emf.ecore.EPackage$Registry').INSTANCE;"
				+ "var className = listLast(class,'/');" + "var packageName = '';" + "if(isObject(class)) {"
				+ "this._instance = class; } else { weee }};";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() == 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
	}
	
	@Test
	public void testParseScriptCfcGood() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/ScriptComponent.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptCfcHarder() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/ScriptComponentHarder.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptCfcWow() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/ScriptComponentWow.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptCfcCrazy() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/ScriptComponentCrazy.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptFw1() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/fw1.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseCFCWithColonMetadata() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/CFCWithColonMetadata.cfc").getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		System.out.println(scriptStatement.toString());
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseAllTestCFCs() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/").getPath();
			File[] files = new File(path).listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("Directory: " + file.getName());
				} else if (file.getPath().endsWith(".cfc")) {
					CFScriptStatement scriptStatement = null;
					try {
						scriptStatement = fCfmlParser.parseScriptFile(file.getAbsolutePath());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						fail("whoops! " + e.getMessage());
					}
					if (fCfmlParser.getMessages().size() > 0) {
						fail("whoops! " + fCfmlParser.getMessages());
					}
					System.out.println(scriptStatement.toString());
					assertNotNull(scriptStatement);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testParseScriptFunction() {
		String script = "function runFunction(functionName,argCol) { runFunk = this[functionName]; results = structNew(); results.result = runFunk(argumentCollection=argCol); results.debug = getDebugMessages(); return results; }";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptEmptyCompDecl() {
		String script = "component { }";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseCompAsArgType() {
		String script = "void function setChild(required component child)  { ArrayAppend(this.children,child); }";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testFunctionWithNamedParamNamedNull() {
		String script = "oNewStarters.addParam(name=\"pager\", null=true, cfsqltype=\"cf_sql_varchar\");";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testFunctionWithRestMetadata() {
		String script = "remote User function getUser(numeric userid restargsource=\"Path\") httpmethod=\"GET\" restpath=\"{userid}\" {}";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testFunctionWithDefaultAndRestMetadata() {
		String script = "remote User function getUser(numeric userid=\"default\" restargsource=\"Path\") httpmethod=\"GET\" restpath=\"{userid}\" {}";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void functionCallHashedParamsSideBySide() {
		String script = "arrayAppend( variables.framework.routes, { '#method##route#' : target } );";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void propertyKeyWordButNotProperty() {
		String script = "public void function onPopulateError( any cfc, string property, struct rc ){}";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void structKeyHashedSideBySide() {
		String script = "funkstruct =  { '#method##route#' : target };";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testParseScriptTryCatch() {
		String script = "try { throw('funk'); } catch (Any e) { woot(); }";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testVoidFunctionComplex() {
		String script = "public void function redirect( string action, string preserve = 'none', string append = 'none', string path = variables.magicBaseURL, string queryString = '', string statusCode = '302' ) { }";
		ScriptBlockContext scriptStatement = parseScript(script);
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testDotNum() {
		String script = "StructKeyExists(FPBSymbolCurrent,\"5\") AND FPBSymbolCurrent.5 EQ \"\";";
		ScriptBlockContext scriptStatement = parseScript(script);
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testDotNum1() {
		String script = "FPBSymbolCurrent.5;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		TestUtils.printCFScriptTree(scriptStatement);
		assertEquals("FPBSymbolCurrent.5", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testFloat1() {
		String script = "x=1.2;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		CFAssignmentExpression assignmentExpr = (CFAssignmentExpression) ((CFExpressionStatement) scriptStatement)
				.getExpression();
		CFLiteral literal = (CFLiteral) assignmentExpr.getRight();
		assertEquals(literal.getToken().getType(), CFSCRIPTLexer.FLOATING_POINT_LITERAL);
		assertEquals("x = 1.2", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testFloat2() {
		String script = "x=.2e10;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		CFAssignmentExpression assignmentExpr = (CFAssignmentExpression) ((CFExpressionStatement) scriptStatement)
				.getExpression();
		CFLiteral literal = (CFLiteral) assignmentExpr.getRight();
		assertEquals(literal.getToken().getType(), CFSCRIPTLexer.FLOATING_POINT_LITERAL);
		assertEquals("x = .2e10", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testFloat3() {
		String script = "x=.2;";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		CFAssignmentExpression assignmentExpr = (CFAssignmentExpression) ((CFExpressionStatement) scriptStatement)
				.getExpression();
		CFLiteral literal = (CFLiteral) assignmentExpr.getRight();
		assertEquals(literal.getToken().getType(), CFSCRIPTLexer.DOT);
		assertEquals("x = .2", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testAndReservedWord() {
		String script = "return Padding & string;";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("return Padding & string", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testAndReservedWord2() {
		String script = "len(string);";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("len(string)", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testAndReservedWord3() {
		String script = "var Array = createObject(\"java\", \"java.lang.reflect.Array\");";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var Array = createObject('java', 'java.lang.reflect.Array')", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testAndReservedWord4() {
		String script = "variables.instance.events.string;";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("variables.instance.events.string", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testMemberExpr() {
		String script = "setNumberOwners(val(arguments.f['ownership_numberOwners']));";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("setNumberOwners(val(arguments.f['ownership_numberOwners']))", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testHangs() {
		String script = "gm.setChangedBy(arguments.user.getFirstName() & \" \" & arguments.user.getMiddleInitial() & \" \" & arguments.user.getLastName() & \" [\" & arguments.user.getUserName() & \"] [\" & arguments.user.getEmail() & \"]\");";
		// String script = "trim(arguments.form.CellNumber);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		// assertEquals("setNumberOwners(val(arguments.f['ownership_numberOwners']))", scriptStatement.Decompile(0));
	}
	
}
