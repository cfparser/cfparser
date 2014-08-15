
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfparam extends GenericStartTagTypeCf {
	protected static final cfparam INSTANCE = new cfparam();

	private cfparam() {
		super("CFML if tag", "<cfparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					