package cfml.dictionary;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestDictionaryContentHandler {
	
	Map<String, Tag> syntaxelements = new HashMap<String, Tag>();
	Map<String, Function> functions = new HashMap<String, Function>();
	Map<String, Object> scopeVars = new HashMap<String, Object>();
	Map<String, Object> scopes = new HashMap<String, Object>();
	
	@Test
	public void test() throws SAXException, ParserConfigurationException, IOException {
		final InputSource input = new InputSource(
				TestDictionaryContentHandler.class.getResourceAsStream("/dictionary/parsing/cf11_sample.xml"));
		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		final XMLReader xmlReader = factory.newSAXParser().getXMLReader();
		
		// setup the content handler and give it the maps for tags and functions
		xmlReader.setContentHandler(new DictionaryContentHandler(syntaxelements, functions, scopeVars, scopes));
		xmlReader.parse(input);
		
		Tag tag = syntaxelements.values().iterator().next();
		assertEquals("variablename", tag.getParameters().iterator().next().getReturnVarType());
		Return returnVal = tag.getReturns().iterator().next();
		assertEquals("name", returnVal.getParameterName());
		assertEquals("Any", returnVal.getType());
		
	}
}
