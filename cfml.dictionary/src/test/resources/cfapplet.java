
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfapplet extends GenericStartTagTypeCf {
	protected static final cfapplet INSTANCE = new cfapplet();

	private cfapplet() {
		super("CFML if tag", "<cfapplet", ">", EndTagType.NORMAL, true, true, false);
	}

}
					