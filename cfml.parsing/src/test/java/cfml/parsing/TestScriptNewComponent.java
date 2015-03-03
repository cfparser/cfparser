package cfml.parsing;

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
	}
	
	@Test
	public void testNewInFunctionMember() {
		String script = "var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package().thing);";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testNewOp() {
		String script = "myvar = new my.path.cfc.Here();";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		script = "var myvar = new my.path.cfc.Here(); var funk=newthing(wee);";
		scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
	}
	
}
