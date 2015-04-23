package cfml.parsing.cfml;

import org.junit.Test;

import cfml.parsing.CFMLSource;

public class TestCFMLParse {
	
	@Test
	public void test() {
		final CFMLSource cfmlSource = new CFMLSource("<cfcomponent><cfset/></cfcomponent>");
		System.out.println(cfmlSource.getAllCFMLTags());
		System.out.println(cfmlSource.getAllElements());
		System.out.println(cfmlSource.getChildElements());
	}
	
	final String cfcSrc = "<cfcomponent>\r\n" + "<cffunction name=\"test\">\r\n"
			+ "	<cfargument name=\"xyz\" default=\"\">\r\n" + "	<cfset xyz=123/>\r\n" + "	<cfset y=arguments.xyz/>\r\n"
			+ "	<cfset z=arguments.xyz/>\r\n" + "</cffunction>\r\n" + "</cfcomponent>";
	
	@Test
	public void test2() {
		final CFMLSource cfmlSource = new CFMLSource(cfcSrc);
		System.out.println(cfmlSource.getAllCFMLTags());
		System.out.println(cfmlSource.getAllElements());
		System.out.println(cfmlSource.getChildElements());
	}
}
