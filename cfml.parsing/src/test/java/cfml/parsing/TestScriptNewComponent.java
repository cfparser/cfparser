package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptNewComponent {
	
	@Test
	public void testNewInFunction() {
		String script = "var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package());";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package())",
				scriptStatement.Decompile(0));
	}
	
	@Test
	public void testNewInFunctionA() {
		String script = "new x.DojoApp().package();";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		TestUtils.printCFScriptTree(scriptStatement);
		assertEquals("new x.DojoApp().package()", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testNewInFunctionMember() {
		String script = "var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package().thing);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package().thing)",
				scriptStatement.Decompile(0));
	}
	
	@Test
	public void testNewOp() {
		String script = "myvar = new my.path.cfc.Here();";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("myvar = new my.path.cfc.Here()", scriptStatement.Decompile(0));
		script = "var myvar = new my.path.cfc.Here(); var funk=newthing(wee);";
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("{  var myvar = new my.path.cfc.Here();  var funk = newthing(wee);}", scriptStatement.Decompile(0)
				.replaceAll("[\r\n]", ""));
	}
	
}
