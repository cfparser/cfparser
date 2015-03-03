package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.utils.TestUtils;

public class TestScriptSwitch {
	
	@Test
	public void testSwitch() {
		String script = "switch (prop.getType()){case 'date':case 'datetime':kronk=sqronk;break;default:flur;}}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("switch(prop.getType()){case 'date':;case 'datetime':kronk=sqronk;break;;default:flur;;}",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testSwitchEmpty() {
		String script = "switch(prop) {case 'trunk' : case 'pleck' : break; case 'strunk' : break;}";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("switch (prop){case 'trunk':case 'pleck':break;case 'strunk':break;}", scriptStatement
				.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
	@Test
	public void testCfmlFunctionStatement() {
		String script = "savecontent variable='renderedcontent' {model = duplicate(_model);metadata = duplicate(_model);};";
		CFScriptStatement scriptStatement = TestUtils.parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("savecontent variable='renderedcontent'{model=duplicate(_model);metadata=duplicate(_model);}",
				scriptStatement.Decompile(0).replaceAll("[\r\n]", ""));
	}
	
}
