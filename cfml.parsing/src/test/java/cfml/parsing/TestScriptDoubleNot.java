package cfml.parsing;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cfml.CFSCRIPTParser.ScriptBlockContext;

public class TestScriptDoubleNot {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Ignore
	@Test
	public void testParseScriptDoubleNot() {
		String script = "if (!! xx) yy = 1;";
		ScriptBlockContext scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScript(script);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(scriptStatement);
	}
}
