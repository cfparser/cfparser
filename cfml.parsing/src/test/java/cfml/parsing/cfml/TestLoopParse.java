package cfml.parsing.cfml;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.CFMLParser;
import cfml.parsing.CFMLSource;

public class TestLoopParse {
	private CFMLParser fCfmlParser;
	
	@Before
	public void setUp() throws Exception {
		fCfmlParser = new CFMLParser();
	}
	
	@Test
	public void test() {
		final CFMLSource cfmlSource = new CFMLSource("<cfloop query=\"test\"><cfset temp = 1/></cfloop>");
		fCfmlParser.parseElements(cfmlSource);
		fCfmlParser.parse();
		System.out.println(fCfmlParser.getDebuggingInfo());
		System.out.println(cfmlSource.getAllElements());
		System.out.println(cfmlSource.getChildElements());
	}
	
}
