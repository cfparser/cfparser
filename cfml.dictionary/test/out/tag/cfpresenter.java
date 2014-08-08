
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfpresenter extends GenericStartTagTypeCf {
	protected static final cfpresenter INSTANCE = new cfpresenter();

	private cfpresenter() {
		super("CFML if tag", "<cfpresenter", ">", EndTagType.NORMAL, true, true, false);
	}

}
					