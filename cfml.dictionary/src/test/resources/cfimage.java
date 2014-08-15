
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfimage extends GenericStartTagTypeCf {
	protected static final cfimage INSTANCE = new cfimage();

	private cfimage() {
		super("CFML if tag", "<cfimage", ">", EndTagType.NORMAL, true, true, false);
	}

}
					