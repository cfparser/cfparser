
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfinclude extends GenericStartTagTypeCf {
	protected static final cfinclude INSTANCE = new cfinclude();

	private cfinclude() {
		super("CFML if tag", "<cfinclude", ">", EndTagType.NORMAL, true, true, false);
	}

}
					