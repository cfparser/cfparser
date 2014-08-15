
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfhtmlhead extends GenericStartTagTypeCf {
	protected static final cfhtmlhead INSTANCE = new cfhtmlhead();

	private cfhtmlhead() {
		super("CFML if tag", "<cfhtmlhead", ">", EndTagType.NORMAL, true, true, false);
	}

}
					