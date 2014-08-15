
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfqueryparam extends GenericStartTagTypeCf {
	protected static final cfqueryparam INSTANCE = new cfqueryparam();

	private cfqueryparam() {
		super("CFML if tag", "<cfqueryparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					