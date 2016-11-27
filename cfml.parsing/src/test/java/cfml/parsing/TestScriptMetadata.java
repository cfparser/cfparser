package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptMetadata {
	
	@Test
	public void testFunction() {
		String script = "function foo() metakey:metaval { };";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("public function foo() metakey=metaval  {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testFunctionMetaACF() {
		String script = "function foo() metakeyprefix:metakey='realval' { };";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("public function foo() metakeyprefix:metakey='realval'  {  }",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
}
