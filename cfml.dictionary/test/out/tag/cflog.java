
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cflog extends GenericStartTagTypeCf {
	protected static final cflog INSTANCE = new cflog();

	private cflog() {
		super("CFML if tag", "<cflog", ">", EndTagType.NORMAL, true, true, false);
	}

}
					