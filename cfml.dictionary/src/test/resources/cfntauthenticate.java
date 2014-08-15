
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfntauthenticate extends GenericStartTagTypeCf {
	protected static final cfntauthenticate INSTANCE = new cfntauthenticate();

	private cfntauthenticate() {
		super("CFML if tag", "<cfntauthenticate", ">", EndTagType.NORMAL, true, true, false);
	}

}
					