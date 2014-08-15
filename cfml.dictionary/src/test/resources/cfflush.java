
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfflush extends GenericStartTagTypeCf {
	protected static final cfflush INSTANCE = new cfflush();

	private cfflush() {
		super("CFML if tag", "<cfflush", ">", EndTagType.NORMAL, true, true, false);
	}

}
					