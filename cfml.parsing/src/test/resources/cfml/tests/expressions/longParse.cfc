<cfscript>
private void function s() {
		var args = {};

		var x		 			= {};
		x.id				= request.x.id;
		x.a					= request.x.a;
		x.b			= request.x.b;
		x.c				= request.x.c;
		x.d				= request.x.d;
		// comment
		module(
			template	= "some.cfm",
			xid 		= x.id
		);
}

</cfscript>