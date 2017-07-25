package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class StartTagTypeCFQueryParam extends StartTagTypeGenericImplementation {
	protected static final StartTagTypeCFQueryParam INSTANCE = new StartTagTypeCFQueryParam();
	
	private StartTagTypeCFQueryParam() {
		super("CFML query param", "<cfqueryparam", ">", EndTagType.NORMAL, true, true, true);
	}
	
	@Override
	protected Tag constructTagAt(final Source source, final int pos) {
		final StartTag startTag = (StartTag) super.constructTagAt(source, pos);
		if (startTag == null)
			return null;
		// A CFML script element requires the attribute language="php".
		// if
		// (!"php".equalsIgnoreCase(startTag.getAttributes().getValue("language")))
		// return null;
		return startTag;
	}
}
