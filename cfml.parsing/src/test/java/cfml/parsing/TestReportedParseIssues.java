package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestReportedParseIssues {
	
	@Test
	public void testIssue24a() {
		String script = "var user = userservice.getuser();";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var user = userservice.getuser()", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue24b() {
		String script = "var user = userservice[\"getuser\"]();";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var user = userservice['getuser']()", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue22a() {
		String script = "url[i];";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("url[i]", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue22b() {
		String script = "url[#i#];";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("url[#i#]", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue22c() {
		String script = "'#i#';";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("'#i#'", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue11a() {
		String script = "if(!!x){};";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("if(!!x )   {\n" + "\n" + "  }", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue11b() {
		String script = "runTestsInTestCase( new \"specs.#testCaseName#\"() );";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("runTestsInTestCase(new 'specs.#testCaseName#'())", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testIssue11c() {
		String script = "arrayEach(dirs, function(d) {\n" + "		directoryDelete(d,true);\n" + "		});";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		// TestUtils.printCFScriptTree(scriptStatement);
		assertEquals("arrayEach(dirs,  function (d)   {directoryDelete(d, true);  })", scriptStatement.Decompile(0)
				.replaceAll("[\n\r]", ""));
	}
	
	@Test
	public void testIssue11c3() {
		String script = "arrayEach(dirs, dirs2);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		String result = scriptStatement.Decompile(0).replaceAll("[\n\r]", "");
		assertEquals("arrayEach(dirs, dirs2)", result);
	}
	
}
