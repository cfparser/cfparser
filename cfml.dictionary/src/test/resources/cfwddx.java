
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfwddx extends GenericStartTagTypeCf {
	protected static final cfwddx INSTANCE = new cfwddx();

	private cfwddx() {
		super("CFML if tag", "<cfwddx", ">", EndTagType.NORMAL, true, true, false);
	}

}
					