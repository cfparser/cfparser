package cfml.parsing.cfmentat.tag;

final class StartTagTypeCfSet extends CFMLStartTag {
	protected static final StartTagTypeCfSet INSTANCE = new StartTagTypeCfSet();
	
	private StartTagTypeCfSet() {
		// super("CFSET","<cfset",">",null,true,false,false);
		super("CFML short tag", "<cfset", ">", null, false, false, false);
	}
}
