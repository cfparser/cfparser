package cfml.parsing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTParser.ScriptBlockContext;

public class TestScriptSwitch {
	
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
	public void testSwitch() {
		String script = "switch(prop.getType()) {case 'date' : case 'datetime' : kronk=sqronk; break; default: flur;}";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testSwitchEmpty() {
		String script = "switch(prop) {case 'trunk' : case 'pleck' : break; case 'strunk' : break;}";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		// TestUtils.showGUI(scriptStatement, CFSCRIPTParser.ruleNames);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testCfmlFunctionStatement() {
		String script = "savecontent variable='renderedcontent' {model = duplicate(_model);metadata = duplicate(_model);};";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
}
