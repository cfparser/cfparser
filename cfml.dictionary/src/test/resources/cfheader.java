
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfheader extends GenericStartTagTypeCf {
	protected static final cfheader INSTANCE = new cfheader();

	private cfheader() {
		super("CFML if tag", "<cfheader", ">", EndTagType.NORMAL, true, true, false);
	}

}
					