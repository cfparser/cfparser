package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFIfStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptParserIfs {
	
	@Test
	public void testIfLongFunkKeyWord() {
		String script = "if(tag.template.startsWith('ram:')) {}";
		CFIfStatement scriptStatement = (CFIfStatement) TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		System.out.println(scriptStatement.Decompile(0));
		assertEquals("if(tag.template.startsWith('ram:') )   {  }",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
		// System.out.println(scriptStatement.getExpression().Decompile(0));
		// System.out.println(scriptStatement.getExpression().getClass());
	}
	
	@Test
	public void testIfLongFunk() {
		String script = "if(foo.bar.blah('ram:')) {}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("if(foo.bar.blah('ram:') )   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testIfshortForm() {
		String script = "if ( structKeyExists( cfc, \"get#property#\" ) ) return evaluate( 'cfc.get#property#()' );";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		System.out.println(scriptStatement.Decompile(0));
		assertEquals("if(structKeyExists(cfc, 'get#property#') )   return evaluate('cfc.get#property#()')",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
}
