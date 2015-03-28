package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.script.CFScriptStatement;

public class TestArraysStructs {
	
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	private CFScriptStatement parseScript(String script) {
		CFScriptStatement scriptStatement = null;
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
	public void testEmptyVaredArray() {
		String script = "var someArry = [];";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals("var someArry = []", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testVaredArrayOfStruct() {
		String script = "var someArry = [{funk:'wee'},{funk2:'wee2'}];";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals("var someArry = [{funk:'wee'},{funk2:'wee2'}]", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testVaredArrayOfStructKyeyWerd() {
		String script = "var package = { name:'cfcPackage', datatypes:datatypes, classes : [{ name:'CFCModel',features : [{name='package', etype:'EString', lowerBound:0,upperBound:1},{name='cfcs', reference:'ORMEntity',containment:true, lowerBound:0,upperBound:-1}]}]};";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals(
				"var package = {name:'cfcPackage',datatypes:datatypes,classes:[{name:'CFCModel',features:[{name:'package',etype:'EString',lowerBound:0,upperBound:1},{name:'cfcs',reference:'ORMEntity',containment:true,lowerBound:0,upperBound:-1}]}]}",
				scriptStatement.Decompile(0));
		;
	}
	
	@Test
	public void testStructOfArray() {
		String script = "var someStrucsArry = {stru:[{funk:'wee'},{funk2:'wee2'}], stro:[{funk:'wee'},{funk2:'wee2'}]};";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals("var someStrucsArry = {stru:[{funk:'wee'},{funk2:'wee2'}],stro:[{funk:'wee'},{funk2:'wee2'}]}",
				scriptStatement.Decompile(0));
	}
	
	@Test
	public void testArrayWithFunction() {
		String script = "arrData[ ArrayLen( arrData ) + 1 ] = { Foo = \"Bar\" };";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals("arrData[ArrayLen(arrData) + 1] = {Foo:'Bar'}", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseNestedStructsAndArraysPart1() {
		String script = "var attrs = cfdict.getDictionaryByVersion(\"ColdFusion9\").getElementAttributes(\"cfproperty\").iterator();";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals(
				"var attrs = cfdict.getDictionaryByVersion('ColdFusion9').getElementAttributes('cfproperty').iterator()",
				scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseNestedStructsAndArraysPart2() {
		String script = "var datatypes = new CFDataTypes().package().datatypes;";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		assertEquals("var datatypes = new CFDataTypes().package().datatypes", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseNestedStructsAndArraysPart3() {
		String script = "cfpropertyAttribute[\"lowerBound\"] = (attr.isRequired()) ? 1 : 0 ;";
		CFScriptStatement scriptStatement = null;
		scriptStatement = parseScript(script);
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		assertNotNull(scriptStatement);
		System.out.println(scriptStatement.Decompile(0));
		assertEquals("cfpropertyAttribute['lowerBound'] = (attr.isRequired())?1:0", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testParseNestedStructsAndArrays() {
		String path = "";
		try {
			path = new URL("file:src/test/resources/cfml/NestedArraysStructs.cfc").getPath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		CFScriptStatement scriptStatement = null;
		try {
			scriptStatement = fCfmlParser.parseScriptFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("whoops! " + e.getMessage());
		}
		if (fCfmlParser.getMessages().size() > 0) {
			fail("whoops! " + fCfmlParser.getMessages());
		}
		
		assertNotNull(scriptStatement);
		System.out.println(scriptStatement.Decompile(0));
	}
	
	@Test
	public void testStructs() {
		String script = "props[prop.getName()] = 1;";
		CFScriptStatement scriptStatement = parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("props[prop.getName()] = 1", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testStructWithHashKey() {
		String script = "x = { \"#method##route#\" = target };";
		CFScriptStatement scriptStatement = parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("x = {'#method##route#':target}", scriptStatement.Decompile(0));
	}
	
	@Test
	public void testStructWithHashKey2() {
		String script = "x={ '#route#' = target };";
		CFScriptStatement scriptStatement = parseScript(script);
		assertNotNull(scriptStatement);
		assertEquals("x = {'#route#':target}", scriptStatement.Decompile(0));
	}
	
}
