
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfabort extends GenericStartTagTypeCf {
	protected static final cfabort INSTANCE = new cfabort();

	private cfabort() {
		super("CFML if tag", "<cfabort", ">", EndTagType.NORMAL, true, true, false);
	}

}
					