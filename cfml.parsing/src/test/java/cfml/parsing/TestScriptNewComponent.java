package cfml.parsing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTParser.ScriptBlockContext;

public class TestScriptNewComponent {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	private ScriptBlockContext parseScript(String script) {
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScript(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		return scriptStatement;
	}
	
	@Test
	public void testNewInFunction() {
		String script = "var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package());";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testNewInFunctionMember() {
		String script = "var dojoAppPackage = ecore.load(new moshen.metamodels.DojoApp().package().thing);";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testNewOp() {
		String script = "myvar = new my.path.cfc.Here();";
		ScriptBlockContext scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		script = "var myvar = new my.path.cfc.Here(); var funk=newthing(wee);";
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
}
