
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfzipparam extends GenericStartTagTypeCf {
	protected static final cfzipparam INSTANCE = new cfzipparam();

	private cfzipparam() {
		super("CFML if tag", "<cfzipparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					