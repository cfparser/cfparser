package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;

final class StartTagTypeCFMLStandard extends CFMLStartTag {
	protected static final StartTagTypeCFMLStandard INSTANCE = new StartTagTypeCFMLStandard();
	
	private StartTagTypeCFMLStandard() {
		super("cf standard tag", "<cf", ">", EndTagType.NORMAL, false, true, false);
	}
}
