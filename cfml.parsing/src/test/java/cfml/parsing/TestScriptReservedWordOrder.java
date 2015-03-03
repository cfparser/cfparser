package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptReservedWordOrder {
	
	@Test
	public void testFuncNameMatchesAccessType() {
		String script = "function package() {}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("public function package()   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testAccessTypeAndFuncNameMatch() {
		String script = "package function package() {}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("package function package()   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testReturnAndAccessTypeAndFuncNameMatch() {
		String script = "package package function package() {}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("package package function package()   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
}
