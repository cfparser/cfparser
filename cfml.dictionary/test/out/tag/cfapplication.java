
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfapplication extends GenericStartTagTypeCf {
	protected static final cfapplication INSTANCE = new cfapplication();

	private cfapplication() {
		super("CFML if tag", "<cfapplication", ">", EndTagType.NORMAL, true, true, false);
	}

}
					