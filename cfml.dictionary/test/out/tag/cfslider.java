
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfslider extends GenericStartTagTypeCf {
	protected static final cfslider INSTANCE = new cfslider();

	private cfslider() {
		super("CFML if tag", "<cfslider", ">", EndTagType.NORMAL, true, true, false);
	}

}
					