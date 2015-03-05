package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptParserForLoop {
	
	@Test
	public void testForIn() {
		String script = "for(widget in thingWithWidgets.getWidgets()) { writeOutput(widget); };";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("for( widget in thingWithWidgets.getWidgets() )   {writeOutput(widget);  }", scriptStatement
				.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testForInWithVar() {
		String script = "for(var widget in thingWithWidgets.getWidgets()) { writeOutput(widget); };";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("for( var widget in thingWithWidgets.getWidgets() )   {writeOutput(widget);  }", scriptStatement
				.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testForInWithPropVar() {
		String script = "for(var prop in displayFields) {}";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("for( var prop in displayFields )   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testForInWithMember() {
		String script = "for(tag in e.tagcontext) {if(tag.template.startsWith('ram:')) {tag.template = templatepath;}}";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals(
				"for( tag in e.tagcontext )   {    if(tag.template.startsWith('ram:') )       {tag.template=templatepath;      };  }",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testForInWithMemberEmpty() {
		String script = "for(tag in e.tagcontext) {}";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("for( tag in e.tagcontext )   {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testForStatement() {
		String script = "for ( i = 1; i <= n; i = i + 1 ) {}";
		CFScriptStatement scriptStatement = null;
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		TestUtils.printCFScriptTree(scriptStatement);
		assertEquals("for(i=1;i <= n;i=i + 1)  {  }", scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
}
