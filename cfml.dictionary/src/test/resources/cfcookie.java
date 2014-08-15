
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfcookie extends GenericStartTagTypeCf {
	protected static final cfcookie INSTANCE = new cfcookie();

	private cfcookie() {
		super("CFML if tag", "<cfcookie", ">", EndTagType.NORMAL, true, true, false);
	}

}
					