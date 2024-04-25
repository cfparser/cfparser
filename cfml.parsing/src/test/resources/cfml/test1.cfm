<html>
	<cffunction name="test">
		<cfargument name="fred" test="test"/>
		<cfscript>
			WriteOutput("FREDFREDFRED");

			var updateArt = new query();
			transaction
        	{
        		updateArt.execute(sql="update art set artistid=4 where artid = 60");
        	}
		</cfscript>
		<cfif thisisatest is 1>
			<cfoutput>asdfasdf</cfoutput>
		</cfif>
	</cffunction>

	<cfset fred = 2>
	<cfset test(fred)/>
	<cfquery name="funstuff1" dbtype="query">
		SELECT BLAH.WOO, BLAH.HOO FROM BLAH
	</cfquery>
	<cffunction name="test" >
		<cfargument name="test" default="#WriteOutput("">"")#"/> <!--- I think this is valid! --->
	</cffunction>
	<cfargument>
	<cfoutput>
		This is a <b>test</b>
	</cfoutput>
	<cfquery name="funstuff2" datasource="blah">
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
	<cfset test = compName::staticVariable >
	<cfset test2 = compName::staticFunction() >
</html>
