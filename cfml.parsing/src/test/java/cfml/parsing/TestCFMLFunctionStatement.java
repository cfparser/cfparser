package cfml.parsing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import cfml.CFSCRIPTParser.ScriptBlockContext;

public class TestCFMLFunctionStatement {
	
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
	public void testCfmlSavecontentFunctionStatement() {
		String script = "savecontent variable='renderedcontent' {}";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testCfmlFunctionStatement() {
		String script = "savecontent variable='renderedcontent' {model = duplicate(_model); metadata = duplicate(_model); INCLUDE '/ram/#randName#';};";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testCfmlFunctionDirectoryStatement() {
		String script = "directory name=\"dir\" directory=dir action=\"list\" fart=\"yep\" ;";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testIncludeStatement() {
		String script = "include \"/ram/#my#\";";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testSettingStatement() {
		String script = "setting requesttimeout=\"333\";";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		script = "setting requesttimeout=333;";
		scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testQueryStatement() {
		String script = "query name=\"funk\" { writeOutput('SELECT * FROM FUNK'); }";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testLongFuncStatement() {
		String script = "var wee = load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(\"*\", XMIResourceFactoryImpl);";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testIncludeWithTemplateStatementFail() {
		/* need to check if this is valid in OBD/ACF */
		String script = "include template=\"/ram/#randName#\";";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() == 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testTransactionStatement() {
		/* need to check if this is valid in OBD/ACF */
		String script = "transaction {}";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
	@Test
	public void testImportStatement() {
		/* need to check if this is valid in OBD/ACF */
		String script = "import projectshen.core.*;";
		ScriptBlockContext scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
	}
	
}
