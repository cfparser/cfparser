
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfthrow extends GenericStartTagTypeCf {
	protected static final cfthrow INSTANCE = new cfthrow();

	private cfthrow() {
		super("CFML if tag", "<cfthrow", ">", EndTagType.NORMAL, true, true, false);
	}

}
					