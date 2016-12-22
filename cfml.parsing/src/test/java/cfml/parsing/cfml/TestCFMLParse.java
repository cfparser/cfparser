package cfml.parsing.cfml;

import static org.junit.Assert.assertEquals;

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
	
	@Test
	public void testComments() {
		final CFMLSource cfmlSource = new CFMLSource("<cfcomponent><!--- x ---><cfset/></cfcomponent>");
		assertEquals("[<cfcomponent><!--- x ---><cfset/></cfcomponent>, <!--- x --->, <cfset/>]",
				cfmlSource.getAllElements().toString());
	}
	
	// @Test
	// public void testContent() {
	// final CFMLSource cfmlSource = new CFMLSource("<cfcomponent><cfsavecontent/></cfcomponent>");
	// assertEquals("[<cfcomponent><cfsavecontent/></cfcomponent>, <cfsavecontent/>]",
	// cfmlSource.getAllElements().toString());
	// }
	
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
	
	@Test
	public void testContinueTag() {
		final String source = "<cfloop query=\"qResults\">\r\n" + "    <cfif refind( \"^\\.\", qResults.name )>\r\n"
				+ "        <cfcontinue>\r\n" + "    </cfif>\r\n" + "</cfloop>";
		final CFMLSource cfmlSource = new CFMLSource(source);
		System.out.println(cfmlSource.getAllCFMLTags());
		System.out.println(cfmlSource.getAllElements());
		System.out.println(cfmlSource.getChildElements());
		
	}
}
