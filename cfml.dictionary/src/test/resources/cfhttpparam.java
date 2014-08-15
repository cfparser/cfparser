
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfhttpparam extends GenericStartTagTypeCf {
	protected static final cfhttpparam INSTANCE = new cfhttpparam();

	private cfhttpparam() {
		super("CFML if tag", "<cfhttpparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					