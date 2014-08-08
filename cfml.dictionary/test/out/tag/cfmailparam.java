
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfmailparam extends GenericStartTagTypeCf {
	protected static final cfmailparam INSTANCE = new cfmailparam();

	private cfmailparam() {
		super("CFML if tag", "<cfmailparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					