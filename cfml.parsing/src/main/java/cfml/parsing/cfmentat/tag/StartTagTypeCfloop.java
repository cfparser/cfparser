package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;

final class StartTagTypeCfloop extends CFMLStartTag {
	protected static final StartTagTypeCfloop INSTANCE = new StartTagTypeCfloop();
	
	private StartTagTypeCfloop() {
		super("CFML loop tag", "<cfllop", ">", EndTagType.NORMAL, false, false, false);
	}
	
}
