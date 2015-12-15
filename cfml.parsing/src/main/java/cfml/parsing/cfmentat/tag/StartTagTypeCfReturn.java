package cfml.parsing.cfmentat.tag;

final class StartTagTypeCfReturn extends CFMLStartTag {
	protected static final StartTagTypeCfReturn INSTANCE = new StartTagTypeCfReturn();
	
	private StartTagTypeCfReturn() {
		// super("CFSET","<cfset",">",null,true,false,false);
		super("CFML short tag", "<cfreturn", ">", null, false, false, false);
	}
}
