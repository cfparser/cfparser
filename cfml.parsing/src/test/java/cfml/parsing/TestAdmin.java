package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestAdmin {
	
	@Test
	public void testAdmin() {
		String script = "admin action=\"fw1_#application.applicationName#_#variables.framework.applicationKey#_#type#_#componentKey#\" type=\"exclusive\" password=\"30\"";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals(
				"admin action='fw1_#application.applicationName#_#variables.framework.applicationKey#_#type#_#componentKey#' password='30' type='exclusive'",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
		
	}
	
	@Test
	public void testAdminComFunc() {
		String script = "component { function foo(){ admin action=\"getRegional\" type=\"test\" password=\"test\" returnVariable=\"rtn\";return returnVariable;}}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals(
				"component {  public function foo()     {amdin action='getRegional' password='test' returnVariable='rtn' type='test';      return returnVariable;    }}",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
		
	}
}
