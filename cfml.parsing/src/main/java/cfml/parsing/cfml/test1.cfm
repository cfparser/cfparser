<html>
	<cffunction name="test">
		<cfargument name="fred" test="test"/>
		<cfscript>
			WriteOutput("FREDFREDFRED");
		</cfscript>
		<cfif thisisatest is 1>
			<cfoutput>asdfasdf</cfoutput>
		</cfif>
	</cffunction>
	<cfloop query="TEST">

	</cfloop>

	<cfset fred = 2>
	<cfset test(fred)/>
	<cfquery name="funstuff" dbtype="query">
		SELECT BLAH.WOO, BLAH.HOO FROM BLAH
	</cfquery>
	<cffunction name="test" >
		<cfargument name="test" default="#WriteOutput("">"")#"/> <!--- I think this is valid! --->
	</cffunction>
	<cfargument>
	<cfoutput>
		This is a <b>test</b>
	</cfoutput>
	<cfquery name="funstuff" datasource="blah">
		SELECT BLAH.WOO, BLAH.HOO FROM BLAH WHERE wee = #hee#
	</cfquery>
	<cfquery>
		SELECT BLAH.WOO, BLAH.HOO FROM BLAH
	</cfquery>
	<table>
		<tr>
			<td style="<cfoutput>#somethinghere#</cfoutput>">asdfasdf</td>
			<td style="fred"></td>
		</td>
	</table>
</html>
