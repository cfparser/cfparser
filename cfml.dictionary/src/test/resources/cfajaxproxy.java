
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfajaxproxy extends GenericStartTagTypeCf {
	protected static final cfajaxproxy INSTANCE = new cfajaxproxy();

	private cfajaxproxy() {
		super("CFML if tag", "<cfajaxproxy", ">", EndTagType.NORMAL, true, true, false);
	}

}
					