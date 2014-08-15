
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cflogout extends GenericStartTagTypeCf {
	protected static final cflogout INSTANCE = new cflogout();

	private cflogout() {
		super("CFML if tag", "<cflogout", ">", EndTagType.NORMAL, true, true, false);
	}

}
					